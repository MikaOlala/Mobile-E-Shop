package kz.springboot.springtask7.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private int price;

    @Column(name = "stars", length = 1)
    private int stars;

    @Column(name = "picture")
    private String picture;

    @Column(name = "pictureL")
    private String pictureL;

    @Column(name = "added")
    private Date added;

    @Column(name = "top")
    private boolean top;

    @ManyToOne(fetch = FetchType.EAGER)
    private Brand brand;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Category> categories;

    public static Comparator<Item> getCompByTop()
    {
        Comparator comp = new Comparator<Item>(){
            @Override
            public int compare(Item abc1, Item abc2) {
                return Boolean.compare(abc2.top,abc1.top);
            }
        };
        return comp;
    }
}
