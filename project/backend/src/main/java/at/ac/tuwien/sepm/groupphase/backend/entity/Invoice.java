package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.type.InvoiceType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.Objects;

@Entity(name = "invoice")
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "reference_nr", "invoice_type" }) })
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "reference_nr") //Would be nice if I had found some better way to autogenerate this
    private Long referenceNr;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "invoice_type", nullable = false)
    private InvoiceType invoiceType;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "ticket_acquisition_id", referencedColumnName = "id")
    private TicketAcquisition ticketAcquisition;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "merch_purchase_id", referencedColumnName = "id")
    private MerchPurchase merchPurchase;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn (nullable = false, name = "address_id", referencedColumnName = "id")
    private Address address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReferenceNr() {
        return referenceNr;
    }

    public void setReferenceNr(Long referenceNr) {
        this.referenceNr = referenceNr;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public TicketAcquisition getTicketAcquisition() {
        return ticketAcquisition;
    }

    public void setTicketAcquisition(TicketAcquisition ticketAcquisition) {
        this.ticketAcquisition = ticketAcquisition;
    }

    public MerchPurchase getMerchPurchase() {
        return merchPurchase;
    }

    public void setMerchPurchase(MerchPurchase merchPurchase) {
        this.merchPurchase = merchPurchase;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Invoice invoice)) {
            return false;
        }
        return Objects.equals(id, invoice.id)
            && Objects.equals(referenceNr, invoice.referenceNr)
            && Objects.equals(date, invoice.date)
            && Objects.equals(invoiceType, invoice.invoiceType)
            && Objects.equals(recipientName, invoice.recipientName)
            && Objects.equals(ticketAcquisition, invoice.ticketAcquisition)
            && Objects.equals(address, invoice.address)
            && Objects.equals(merchPurchase, invoice.merchPurchase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, referenceNr, date, invoiceType, recipientName, ticketAcquisition, merchPurchase, address);
    }

    @Override
    public String toString() {
        return "Invoice{"
            + "id=" + id
            + ", referenceNr=" + referenceNr
            + ", date=" + date
            + ", invoiceType=" + invoiceType
            + ", recipientName=" + recipientName
            + ", ticketAcquisition=" + ticketAcquisition
            + ", merchPurchase=" + merchPurchase
            + ", address=" + address
            + '}';
    }

    public static final class InvoiceBuilder {
        private Long id;
        private Long referenceNr;
        private LocalDate date;
        private InvoiceType invoiceType;
        private String recipientName;
        private TicketAcquisition ticketAcquisition;
        private MerchPurchase merchPurchase;
        private Address address;

        private InvoiceBuilder() {
        }

        public static InvoiceBuilder anInvoice() {
            return new InvoiceBuilder();
        }

        public InvoiceBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public InvoiceBuilder withReferenceNr(Long referenceNr) {
            this.referenceNr = referenceNr;
            return this;
        }

        public InvoiceBuilder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public InvoiceBuilder withInvoiceType(InvoiceType invoiceType) {
            this.invoiceType = invoiceType;
            return this;
        }

        public InvoiceBuilder withRecipientName(String recipientName) {
            this.recipientName = recipientName;
            return this;
        }

        public InvoiceBuilder withTicketAcquisition(TicketAcquisition ticketAcquisition) {
            this.ticketAcquisition = ticketAcquisition;
            return this;
        }

        public InvoiceBuilder withMerchPurchase(MerchPurchase merchPurchase) {
            this.merchPurchase = merchPurchase;
            return this;
        }

        public InvoiceBuilder withAddress(Address address) {
            this.address = address;
            return this;
        }

        public Invoice build() {
            Invoice invoice = new Invoice();
            invoice.setId(id);
            invoice.setReferenceNr(referenceNr);
            invoice.setDate(date);
            invoice.setInvoiceType(invoiceType);
            invoice.setRecipientName(recipientName);
            invoice.setTicketAcquisition(ticketAcquisition);
            invoice.setMerchPurchase(merchPurchase);
            invoice.setAddress(address);
            return invoice;
        }
    }
}