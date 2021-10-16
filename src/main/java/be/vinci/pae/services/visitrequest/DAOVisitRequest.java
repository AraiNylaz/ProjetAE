package be.vinci.pae.services.visitrequest;

import java.sql.Timestamp;
import java.util.List;
import be.vinci.pae.domain.visitrequest.VisitRequestDTO;

public interface DAOVisitRequest {

  int addVisitRequest(VisitRequestDTO visitRequest);

  VisitRequestDTO selectVisitRequestById(int id);

  List<VisitRequestDTO> getAllVisitsOpenned();

  List<VisitRequestDTO> selectVisitRequestByUserId(int userId);

  boolean cancelVisitRequest(int id, String cancellationReason);

  boolean chooseDateForVisit(int id, Timestamp chosenDateTime);

}
