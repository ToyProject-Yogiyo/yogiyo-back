package toy.yogiyo.core.Order.dto;

import lombok.*;
import toy.yogiyo.core.Order.domain.Order;
import toy.yogiyo.core.Order.domain.OrderType;
import toy.yogiyo.core.Order.domain.Status;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderHistory {

    private Long orderId;
    private LocalDateTime orderTime;
    private OrderType orderType;
    private Status status;
    private Long shopId;
    private String shopName;
    private String shopImg;
    private String menuName;
    private int menuCount;
    private int totalMenuCount;

    public static OrderHistory from(Order order){
        return OrderHistory.builder()
                .orderId(order.getId())
                .orderTime(order.getCreatedAt())
                .orderType(order.getOrderType())
                .status(order.getStatus())
                .shopName(order.getShop().getName())
                .shopId(order.getShop().getId())
                .shopImg(order.getShop().getIcon())
                .menuName(order.getOrderItems().get(0).getMenuName())
                .menuCount(order.getOrderItems().get(0).getQuantity())
                .totalMenuCount(order.getOrderItems().size())
                .build();
    }

}
