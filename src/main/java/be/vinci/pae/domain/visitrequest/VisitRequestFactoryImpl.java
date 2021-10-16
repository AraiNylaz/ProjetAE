package be.vinci.pae.domain.visitrequest;

public class VisitRequestFactoryImpl implements VisitRequestFactory {

  @Override
  public VisitRequest getVisitRequest() {
    return new VisitRequestImpl();
  }

}
