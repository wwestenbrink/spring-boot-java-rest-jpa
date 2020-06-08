package de.wwestenbrink.bootstrap;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.wwestenbrink.bootstrap.model.Asset;
import de.wwestenbrink.bootstrap.repository.AssetRepository;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"use-h2-db"})
@ContextConfiguration(classes = Application.class)
class AssetsApiTest {

  public static final String API_ROOT = "/version/v1/assets";
  public static final String UNKNOWN_NAME = "unknown_name";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private AssetRepository assetRepository;

  @Test
  public void callListAssets() throws Exception {
    assetRepository.deleteAll();

    mockMvc.perform(get(API_ROOT + "/"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  public void callCreateAsset() throws Exception {
    Asset testAsset = createTestAsset();

    mockMvc.perform(generatePostRequest(testAsset))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value(testAsset.getName()))
        .andExpect(jsonPath("$.value").value(testAsset.getValue()));
  }

  @Test
  public void callCreateAssetWithInvalidName() throws Exception {
    final Asset asset = createTestAsset();
    asset.setName(null);

    mockMvc.perform(generatePostRequest(asset))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void callCreateAssetWithSameName() throws Exception {
    Asset testAsset = createTestAsset();
    mockMvc.perform(generatePostRequest(testAsset));

    mockMvc.perform(generatePostRequest(testAsset))
        .andExpect(status().isConflict());
  }

  @Test
  public void callGetAssetById() throws Exception {
    MvcResult postResult = mockMvc.perform(generatePostRequest(createTestAsset())).andReturn();
    final Asset savedAsset =
        objectMapper.readValue(postResult.getResponse().getContentAsString(), Asset.class);

    MockHttpServletRequestBuilder getRequest = get(API_ROOT + "/" + savedAsset.getId());
    mockMvc.perform(getRequest)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(savedAsset.getId()));
  }

  @Test
  public void callGetAssetByUnknownId() throws Exception {
    assetRepository.deleteAll();
    long unknownId = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);

    mockMvc.perform(get(API_ROOT + "/" + unknownId)).andExpect(status().isNotFound());
  }

  @Test
  public void callGetAssetByName() throws Exception {
    MvcResult postResult = mockMvc.perform(generatePostRequest(createTestAsset())).andReturn();
    final Asset savedAsset =
        objectMapper.readValue(postResult.getResponse().getContentAsString(), Asset.class);

    MockHttpServletRequestBuilder getRequest = get(API_ROOT + "/name/" + savedAsset.getName());
    mockMvc.perform(getRequest)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(savedAsset.getName()));
  }

  @Test
  public void callGetAssetByUnknownName() throws Exception {
    assetRepository.deleteAll();

    mockMvc.perform(get(API_ROOT + "/name/" + UNKNOWN_NAME))
        .andExpect(status().isNotFound());
  }

  @Test
  public void callUpdateAsset() throws Exception {
    MvcResult postResult = mockMvc.perform(generatePostRequest(createTestAsset())).andReturn();
    final Asset asset =
        objectMapper.readValue(postResult.getResponse().getContentAsString(), Asset.class);

    final Long new_value = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
    asset.setValue(new_value);

    final String new_name = UUID.randomUUID().toString();
    asset.setName(new_name);

    MockHttpServletRequestBuilder putRequest =
        put(API_ROOT + "/" + asset.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(asset));

    mockMvc.perform(putRequest)
        .andExpect(jsonPath("$.name").value(new_name))
        .andExpect(jsonPath("$.value").value(new_value));
  }

  @Test
  public void callDeleteAsset() throws Exception {
    MvcResult postResult = mockMvc.perform(generatePostRequest(createTestAsset())).andReturn();
    final Asset savedAsset =
        objectMapper.readValue(postResult.getResponse().getContentAsString(), Asset.class);
    String assetUri = API_ROOT + "/" + savedAsset.getId();

    mockMvc.perform(delete(assetUri)).andExpect(status().isOk());
    mockMvc.perform(get(assetUri)).andExpect(status().isNotFound());
  }

  private Asset createTestAsset() {
    String randomName = UUID.randomUUID().toString();
    long randomValue = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);

    return new Asset(null, randomName, randomValue);
  }

  private MockHttpServletRequestBuilder generatePostRequest(Asset asset) throws Exception {
    return post(API_ROOT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(asset));
  }
}
