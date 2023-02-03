package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public class ShoppingCartDto {
    private MerchPurchaseDto merchPurchase;
    private TicketAcquisitionDetailsDto ticketAcquisition;
    private Integer size;
    private Long bonusPoints;
    private CreateInvoiceDto invoice;

    public ShoppingCartDto() {
    }

    public MerchPurchaseDto getMerchPurchase() {
        return merchPurchase;
    }

    public void setMerchPurchase(MerchPurchaseDto merchPurchase) {
        this.merchPurchase = merchPurchase;
    }

    public TicketAcquisitionDetailsDto getTicketAcquisition() {
        return ticketAcquisition;
    }

    public void setTicketAcquisition(TicketAcquisitionDetailsDto ticketAcquisition) {
        this.ticketAcquisition = ticketAcquisition;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(Long bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

    public CreateInvoiceDto getInvoice() {
        return invoice;
    }

    public void setInvoice(CreateInvoiceDto invoice) {
        this.invoice = invoice;
    }

    @Override
    public String toString() {
        return "ShoppingCartDto{"
                + "merchPurchase=" + merchPurchase
                + ", ticketAcquisition=" + ticketAcquisition
                + ", size=" + size
                + ", bonusPoints=" + bonusPoints
                + ", invoice=" + invoice
                + '}';
    }
}
