package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.service.InvoiceService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import com.lowagie.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = InvoiceEndpoint.BASE_PATH)
public class InvoiceEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/invoice";
    private final InvoiceService invoiceService;
    private final UserService userService;

    @Autowired
    public InvoiceEndpoint(InvoiceService invoiceService, UserService userService) {
        this.invoiceService = invoiceService;
        this.userService = userService;
    }

    @Secured("ROLE_USER")
    @GetMapping("unspecified/{id}")
    @Transactional
    @Operation(summary = "Sends a PDF of the requested invoice/cancellation", security = @SecurityRequirement(name = "apiKey"))
    public void getById(HttpServletResponse response, @PathVariable long id) throws DocumentException, IOException {
        LOGGER.info("GET " + BASE_PATH + "/unspecified/{}", id);
        LOGGER.debug("getById({}, {})", response, id);
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userService.findApplicationUserByEmail(username).getId();

        response.setContentType("application/pdf");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=invoice_" + id +  ".pdf";
        response.setHeader(headerKey, headerValue);

        invoiceService.getById(response, id, userId);
    }

    @Secured("ROLE_USER")
    @GetMapping("regular/{referenceNr}")
    @Transactional
    @Operation(summary = "Sends a PDF of the requested invoice", security = @SecurityRequirement(name = "apiKey"))
    public void getInvoiceById(HttpServletResponse response, @PathVariable long referenceNr) throws DocumentException, IOException {
        LOGGER.info("GET " + BASE_PATH + "/regular/{}", referenceNr);
        LOGGER.debug("getInvoiceById({}, {})", response, referenceNr);
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userService.findApplicationUserByEmail(username).getId();

        response.setContentType("application/pdf");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=invoice_" + referenceNr +  ".pdf";
        response.setHeader(headerKey, headerValue);

        invoiceService.getInvoiceById(response, referenceNr, userId);
    }

    @Secured("ROLE_USER")
    @GetMapping("cancellation/{referenceNr}")
    @Transactional
    @Operation(summary = "Sends a PDF of the requested cancellation", security = @SecurityRequirement(name = "apiKey"))
    public void getCancellationById(HttpServletResponse response, @PathVariable long referenceNr) throws DocumentException, IOException {
        LOGGER.info("GET " + BASE_PATH + "/cancellation/{}", referenceNr);
        LOGGER.debug("getCancellationById({}, {})", response, referenceNr);
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userService.findApplicationUserByEmail(username).getId();

        response.setContentType("application/pdf");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=cancellation_" + referenceNr + ".pdf";
        response.setHeader(headerKey, headerValue);

        invoiceService.getCancellationById(response, referenceNr, userId);
    }

}
