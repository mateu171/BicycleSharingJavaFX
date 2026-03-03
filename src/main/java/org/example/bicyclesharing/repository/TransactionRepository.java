package org.example.bicyclesharing.repository;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Transaction;

public interface TransactionRepository extends Repository<Transaction, UUID>{

  public List<Transaction> findByUserId(UUID id);

}
