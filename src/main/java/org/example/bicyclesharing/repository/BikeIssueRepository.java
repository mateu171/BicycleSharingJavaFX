package org.example.bicyclesharing.repository;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.BikeIssue;
import org.example.bicyclesharing.domain.enums.IssueStatus;

public interface BikeIssueRepository extends Repository<BikeIssue,UUID> {

   List<BikeIssue> findByStatus(IssueStatus status);

   List<BikeIssue> findByBicycleId(UUID bicycleId);
}
