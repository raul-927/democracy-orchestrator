package demo.services;

import demo.domain.Order;

public interface OrderService {
    Order newOrder(Order order);
    void initOrderSaga();
    void validateOrder(Order order);
    void payOrder();
    void shipOrder();
    void completeOrder();
    void stopOrderSaga();
}
