package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateInvoiceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ShoppingCartDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

public interface ShoppingCartService {
    /**
     * Store merch articles and tickets within the shopping cart as purchases.
     *
     * @param invoiceDto billing information of a user
     * @return the items that were stored within the shopping cart
     * @throws ValidationException if the cart is empty
     * @throws ConflictException if expected entities do not exist or limits are not upheld
     */
    ShoppingCartDto finalizePurchase(CreateInvoiceDto invoiceDto) throws ValidationException, ConflictException;

    /**
     * Retrieves merch articles and tickets that are not within finalized merch or ticket purchases.
     *
     * @return the current shopping cart of a user
     */
    ShoppingCartDto getShoppingCart();
}
