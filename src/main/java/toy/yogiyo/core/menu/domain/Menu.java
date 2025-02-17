package toy.yogiyo.core.menu.domain;

import lombok.*;
import toy.yogiyo.core.menuoption.domain.OptionGroupLinkMenu;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {

    @Id @GeneratedValue
    @Column(name = "menu_id")
    private Long id;

    private String name;
    private String content;
    private String picture;
    private Integer price;

    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_group_id")
    private MenuGroup menuGroup;

    @Builder.Default
    @OneToMany(mappedBy = "menu", cascade = CascadeType.REMOVE)
    private List<OptionGroupLinkMenu> linkMenus = new ArrayList<>();

    public void changePicture(String picture) {
        this.picture = picture;
    }

    public void changeInfo(Menu updateParam) {
        this.name = updateParam.getName();
        this.content = updateParam.getContent();
        this.price = updateParam.getPrice();
    }

    public void changePosition(int position) {
        this.position = position;
    }

    public void changeMenuGroup(MenuGroup menuGroup) {
        this.menuGroup = menuGroup;
    }

}
