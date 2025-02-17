package toy.yogiyo.core.Address.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.yogiyo.core.Member.domain.Member;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAddress {

    @Id @GeneratedValue
    private Long id;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;
    private String nickname;

    private Double longitude;
    private Double latitude;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public MemberAddress(Long id, Address address, AddressType addressType, String nickname, Double longitude, Double latitude, Member member) {
        this.id = id;
        this.address = address;
        this.addressType = addressType;
        this.nickname = nickname;
        this.longitude = longitude;
        this.latitude = latitude;
        this.member = member;
    }

    public void setMember(Member member){
        this.member = member;
    }
}
