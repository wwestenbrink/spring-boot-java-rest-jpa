package de.wwestenbrink.bootstrap.controller;

import de.wwestenbrink.bootstrap.exception.AssetAlreadyExistsException;
import de.wwestenbrink.bootstrap.exception.AssetNotFoundException;
import de.wwestenbrink.bootstrap.model.Asset;
import de.wwestenbrink.bootstrap.repository.AssetRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/version/v1")
@Slf4j
@Validated
@RequiredArgsConstructor
@Tag(name = "Assets")
public class AssetController {
  public static final String ASSETS_ROUTE = "/assets";
  private final AssetRepository assetRepository;

  @Operation(summary = "List all assets")
  @GetMapping(ASSETS_ROUTE)
  public Iterable<Asset> list() {
    log.info("Listing assets");
    return assetRepository.findAll();
  }

  @Operation(summary = "Create asset")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Asset created"),
      @ApiResponse(responseCode = "409", description = "Asset with this name already exists")})
  @PostMapping(ASSETS_ROUTE)
  @ResponseStatus(HttpStatus.CREATED)
  public Asset create(@Valid @RequestBody Asset asset) {
    log.info("Creating asset");
    if (assetRepository.findByName(asset.getName()).isPresent()) {
      throw new AssetAlreadyExistsException();
    }
    return assetRepository.save(asset);
  }

  @Operation(summary = "Get asset")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Asset found"),
      @ApiResponse(responseCode = "404", description = "Asset with provided id not found")})
  @GetMapping(ASSETS_ROUTE + "/{id}")
  public Asset findById(@Valid @PathVariable Long id) {
    log.info("Finding asset by id {}", id);
    return assetRepository.findById(id).orElseThrow(AssetNotFoundException::new);
  }

  @Operation(summary = "Get asset by name")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Asset found"),
      @ApiResponse(responseCode = "404", description = "Asset with provided name not found")})
  @GetMapping(ASSETS_ROUTE + "/name/{name}")
  public Asset findByName(@PathVariable String name) {
    log.info("Finding asset by name {}", name);
    return assetRepository.findByName(name).orElseThrow(AssetNotFoundException::new);
  }

  @Operation(summary = "Update asset")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Asset updated"),
      @ApiResponse(responseCode = "404", description = "Asset with provided id not found")})
  @PutMapping(ASSETS_ROUTE + "/{id}")
  public Asset updateAsset(@Valid @RequestBody Asset asset, @PathVariable Long id) {
    log.info("Updating asset with id {}", id);
    assetRepository.findById(id).orElseThrow(AssetNotFoundException::new);
    asset.setId(id);
    return assetRepository.save(asset);
  }

  @Operation(summary = "Delete asset")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Asset deleted"),
      @ApiResponse(responseCode = "404", description = "Asset with provided id not found")})
  @DeleteMapping(ASSETS_ROUTE + "/{id}")
  public void delete(@PathVariable Long id) {
    log.info("Deleting asset with id {} ", id);
    assetRepository.findById(id).orElseThrow(AssetNotFoundException::new);
    assetRepository.deleteById(id);
  }
}
