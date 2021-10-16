package be.vinci.pae.services.visitrequest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import be.vinci.pae.domain.visitrequest.VisitRequestDTO;
import be.vinci.pae.domain.visitrequest.VisitRequestFactory;
import be.vinci.pae.services.DalBackendServices;
import be.vinci.pae.services.address.DAOAddress;
import be.vinci.pae.services.user.DAOUser;
import be.vinci.pae.utils.FatalException;
import be.vinci.pae.utils.ValueLink.VisitRequestStatus;
import jakarta.inject.Inject;


public class DAOVisitRequestImpl implements DAOVisitRequest {

  private String queryAddVisitRequest;
  private String querySelectAllVisitsOpenned;
  private String querySelectVisitRequestById;
  private String querySelectVisitRequestByUserId;
  private String queryUpdateCancelVisitRequest;
  private String queryUpdateChooseDateForVisit;

  @Inject
  private DalBackendServices dalServices;

  @Inject
  private VisitRequestFactory visitRequestFactory;

  @Inject
  private DAOAddress daoAddress;

  @Inject
  private DAOUser daoUser;

  /**
   * constructor of DAOVisitRequestImpl. contains queries.
   */
  public DAOVisitRequestImpl() {
    queryAddVisitRequest =
        "INSERT INTO project.visit_requests (visit_request_id, request_date, time_slot, address,"
            + " status, chosen_date_time, cancellation_reason, customer) VALUES"
            + " (DEFAULT,?,?,?,?,NULL,NULL,?)";
    querySelectAllVisitsOpenned = "SELECT v.visit_request_id, v.request_date, v.time_slot, "
        + "v.address, v.status, v.chosen_date_time, v.cancellation_reason, v.customer "
        + "FROM project.visit_requests v WHERE v.status  = ? OR (v.status  = ? "
        + "AND v.chosen_date_time > ?) ORDER BY v.request_date";
    querySelectVisitRequestById = "SELECT v.visit_request_id, v.request_date, v.time_slot, "
        + "v.address, v.status, v.chosen_date_time, v.cancellation_reason, v.customer "
        + "FROM project.visit_requests v WHERE v.visit_request_id  = ?";
    querySelectVisitRequestByUserId = "SELECT v.visit_request_id, v.request_date, v.time_slot,"
        + "v.address, v.status, v.chosen_date_time, v.cancellation_reason, v.customer "
        + "FROM project.visit_requests v WHERE v.customer = ? ORDER BY v.request_date";
    queryUpdateCancelVisitRequest = "UPDATE project.visit_requests SET status = ?, "
        + "cancellation_reason = ? WHERE visit_request_id = ?";
    queryUpdateChooseDateForVisit = "UPDATE project.visit_requests SET status = ?, "
        + "chosen_date_time = ? WHERE visit_request_id = ?";
  }

  @Override
  public int addVisitRequest(VisitRequestDTO visitRequest) {
    int visitRequestId = -1;
    try {
      PreparedStatement insertVisitRequest =
          this.dalServices.getPreparedStatementAdd(queryAddVisitRequest);
      insertVisitRequest.setTimestamp(1, Timestamp.from(visitRequest.getRequestDate().toInstant()));
      insertVisitRequest.setString(2, visitRequest.getTimeSlot());
      if (visitRequest.getAddress() == null) {
        insertVisitRequest.setNull(3, java.sql.Types.INTEGER);
      } else {
        insertVisitRequest.setInt(3, visitRequest.getAddress().getId());
      }
      insertVisitRequest.setInt(4, visitRequest.getStatus().ordinal());
      insertVisitRequest.setInt(5, visitRequest.getCustomer().getId());
      insertVisitRequest.execute();
      try (ResultSet rs = insertVisitRequest.getGeneratedKeys()) {
        if (rs.next()) {
          visitRequestId = rs.getInt(1);
        }
        return visitRequestId;
      }
    } catch (SQLException e) {
      throw new FatalException("Data error : insertVisitRequest", e);
    }
  }

  @Override
  public VisitRequestDTO selectVisitRequestById(int id) {
    try {
      PreparedStatement selectVisitRequestById =
          dalServices.getPreparedStatement(querySelectVisitRequestById);
      selectVisitRequestById.setInt(1, id);
      try (ResultSet rs = selectVisitRequestById.executeQuery()) {
        return createVisitRequest(rs);
      }
    } catch (SQLException e) {
      throw new FatalException("Data error : selectVisitRequestById", e);
    }
  }

  @Override
  public List<VisitRequestDTO> getAllVisitsOpenned() {
    try {
      PreparedStatement selectAllVisitsOpenned =
          dalServices.getPreparedStatement(querySelectAllVisitsOpenned);
      selectAllVisitsOpenned.setInt(1, VisitRequestStatus.en_attente.ordinal());
      selectAllVisitsOpenned.setInt(2, VisitRequestStatus.confirmee.ordinal());
      selectAllVisitsOpenned.setTimestamp(3, Timestamp.from(Instant.now()));
      List<VisitRequestDTO> list = new ArrayList<VisitRequestDTO>();
      try (ResultSet rs = selectAllVisitsOpenned.executeQuery()) {
        VisitRequestDTO visit;
        do {
          visit = createVisitRequest(rs);
          list.add(visit);
        } while (visit != null);
        list.remove(list.size() - 1);
      }
      return list;
    } catch (Exception e) {
      throw new FatalException("Data error : getAllVisitsOpenned", e);
    }
  }

  @Override
  public List<VisitRequestDTO> selectVisitRequestByUserId(int userId) {
    try {
      PreparedStatement selectVisitRequestByUserId =
          dalServices.getPreparedStatement(querySelectVisitRequestByUserId);
      selectVisitRequestByUserId.setInt(1, userId);
      List<VisitRequestDTO> list = new ArrayList<VisitRequestDTO>();
      try (ResultSet rs = selectVisitRequestByUserId.executeQuery()) {
        VisitRequestDTO visit;
        do {
          visit = createVisitRequest(rs);
          list.add(visit);
        } while (visit != null);
        list.remove(list.size() - 1);
      }
      return list;
    } catch (Exception e) {
      throw new FatalException("Data error : getVisitRequestByUserId", e);
    }
  }

  @Override
  public boolean cancelVisitRequest(int id, String cancellationReason) {
    try {
      PreparedStatement updateCancelVisitRequest =
          dalServices.getPreparedStatement(queryUpdateCancelVisitRequest);
      updateCancelVisitRequest.setInt(1, VisitRequestStatus.annulee.ordinal());
      updateCancelVisitRequest.setString(2, cancellationReason);
      updateCancelVisitRequest.setInt(3, id);
      return updateCancelVisitRequest.executeUpdate() == 1;
    } catch (Exception e) {
      throw new FatalException("Data error : getAllVisitsOpenned", e);
    }
  }

  @Override
  public boolean chooseDateForVisit(int id, Timestamp chosenDateTime) {
    try {
      PreparedStatement updateChooseDateForVisit =
          dalServices.getPreparedStatement(queryUpdateChooseDateForVisit);
      updateChooseDateForVisit.setInt(1, VisitRequestStatus.confirmee.ordinal());
      updateChooseDateForVisit.setTimestamp(2, chosenDateTime);
      updateChooseDateForVisit.setInt(3, id);
      return updateChooseDateForVisit.executeUpdate() == 1;
    } catch (Exception e) {
      throw new FatalException("Data error : getAllVisitsOpenned", e);
    }
  }

  private VisitRequestDTO createVisitRequest(ResultSet rs) throws SQLException {
    VisitRequestDTO visitRequest = null;
    if (rs.next()) {
      visitRequest = this.visitRequestFactory.getVisitRequest();
      visitRequest.setId(rs.getInt("visit_request_id"));
      visitRequest.setRequestDate(rs.getDate("request_date"));
      visitRequest.setTimeSlot(rs.getString("time_slot"));
      visitRequest.setAddress(daoAddress.getAddressById(rs.getInt("address")));
      visitRequest.setStatus(VisitRequestStatus.values()[rs.getInt("status")]);
      visitRequest.setChosenDateTime(rs.getDate("chosen_date_time"));
      visitRequest.setCancellationReason(rs.getString("cancellation_reason"));
      visitRequest.setCustomer(this.daoUser.getUserById(rs.getInt("customer")));
    }
    return visitRequest;
  }

}
