package toy.yogiyo.core.Review.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ShopReviewScrollRequest {
    private Long shopId;
}
