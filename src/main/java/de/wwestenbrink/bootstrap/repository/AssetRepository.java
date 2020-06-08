package de.wwestenbrink.bootstrap.repository;

import de.wwestenbrink.bootstrap.model.Asset;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends CrudRepository<Asset, Long> {

  Optional<Asset> findByName(String assetName);
}
