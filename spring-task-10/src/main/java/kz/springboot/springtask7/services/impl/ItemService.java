package kz.springboot.springtask7.services.impl;

import kz.springboot.springtask7.entities.*;
import java.util.List;

public interface ItemService {
    Item addItem(Item item);
    List<Item> getItems();
    Item getItem(Long id);
    void deleteItem(Item item);
    Item editItem(Item item);

    List<Item> getItemsBy(String name);
    List<Item> getItemsByDesc(String name);
    List<Item> getItemsByPrice(String name, int from, int to);
    List<Item> getItemsByPriceDesc(String name, int from, int to);

    List<Item> getItemsByBrand(String name, Long id);
    List<Item> getItemsByBrandDesc(String name, Long id);
    List<Item> getItemsByBrandAndPrice(String name, Long id, int from, int to);
    List<Item> getItemsByBrandAndPriceDesc(String name, Long id, int from, int to);

    List<Country> getCountries();
    Country addCountry(Country country);
    Country saveCountry(Country country);
    Country getCountry(Long id);
    void deleteCountry(Country country);

    List<Brand> getBrands();
    Brand addBrand(Brand brand);
    Brand saveBrand(Brand brand);
    Brand getBrand(Long id);
    void deleteBrand(Brand brand);

    List<Category> getCategories();
    Category getCategory(Long id);
    Category saveCategory(Category category);
    Category addCategory(Category category);
    void deleteCategory(Category category);

    List<Picture> getPictures();
    List<Picture> getPicturesByItem(Long id);
    List<Picture> getPictureByNullItem();
    Picture getPicture(Long id);
    Picture addPicture(Picture picture);
    Picture savePicture(Picture picture);
    void deletePicture(Picture picture);

    Basket addBasket(Basket basket);
    List<Basket> getBasket();

}
