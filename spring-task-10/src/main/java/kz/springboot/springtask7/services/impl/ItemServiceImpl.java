package kz.springboot.springtask7.services.impl;

import kz.springboot.springtask7.entities.*;
import kz.springboot.springtask7.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService{

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private BasketRepository basketRepository;


    @Override
    public Item addItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public List<Item> getItems() {
        return itemRepository.findAllByOrderByTopDesc();
    }

    @Override
    public Item getItem(Long id) {
        return itemRepository.getOne(id);
    }

    @Override
    public void deleteItem(Item item) {
        itemRepository.delete(item);
    }

    @Override
    public Item editItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public List<Item> getItemsBy(String name){ return itemRepository.findAllByNameLikeOrderByPriceAsc(name); }
    @Override
    public List<Item> getItemsByDesc(String name){ return itemRepository.findAllByNameLikeOrderByPriceDesc(name); }

    @Override
    public List<Item> getItemsByPrice(String name, int from, int to) {return itemRepository.findAllByNameLikeAndPriceBetweenOrderByPriceAsc(name, from, to);}
    @Override
    public List<Item> getItemsByPriceDesc(String name, int from, int to) {return itemRepository.findAllByNameLikeAndPriceBetweenOrderByPriceDesc(name, from, to);}

    @Override
    public List<Item> getItemsByBrand(String name, Long id) {return itemRepository.findAllByNameLikeAndBrandIdOrderByPriceAsc(name, id);}
    @Override
    public List<Item> getItemsByBrandDesc(String name, Long id) {return itemRepository.findAllByNameLikeAndBrandIdOrderByPriceDesc(name, id);}
    @Override
    public List<Item> getItemsByBrandAndPrice(String name, Long id, int from, int to) {return itemRepository.findAllByNameLikeAndBrandIdAndPriceBetweenOrderByPriceAsc(name, id, from, to);}
    @Override
    public List<Item> getItemsByBrandAndPriceDesc(String name, Long id, int from, int to) {return itemRepository.findAllByNameLikeAndBrandIdAndPriceBetweenOrderByPriceDesc(name, id, from, to);}


    @Override
    public List<Country> getCountries() {
        return countryRepository.findAll();
    }

    @Override
    public Country addCountry(Country country) {
        return countryRepository.save(country);
    }

    @Override
    public Country saveCountry(Country country) {
        return countryRepository.save(country);
    }

    @Override
    public Country getCountry(Long id) {
        return countryRepository.getOne(id);
    }

    @Override
    public void deleteCountry(Country country) {
        countryRepository.delete(country);
    }

    @Override
    public List<Brand> getBrands() {
        return brandRepository.findAll();
    }

    @Override
    public Brand addBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    @Override
    public Brand saveBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    @Override
    public Brand getBrand(Long id) {
        return brandRepository.getOne(id);
    }

    @Override
    public void deleteBrand(Brand brand) {
        brandRepository.delete(brand);
    }

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategory(Long id) {
        return categoryRepository.getOne(id);
    }

    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Category category) {
        categoryRepository.delete(category);
    }

    @Override
    public List<Picture> getPictures() {
        return pictureRepository.findAll();
    }

    @Override
    public List<Picture> getPicturesByItem(Long id) {
        return pictureRepository.findAllByItemId(id);
    }

    @Override
    public List<Picture> getPictureByNullItem() {
        return pictureRepository.findAllByItemIsNull();
    }

    @Override
    public Picture getPicture(Long id) {
        return pictureRepository.getOne(id);
    }


    @Override
    public Picture addPicture(Picture picture) {
        return pictureRepository.save(picture);
    }

    @Override
    public Picture savePicture(Picture picture) {
        return pictureRepository.save(picture);
    }

    @Override
    public void deletePicture(Picture picture) {
        pictureRepository.delete(picture);
    }

    @Override
    public Basket addBasket(Basket basket) {
        return basketRepository.save(basket);
    }

    @Override
    public List<Basket> getBasket() {
        return basketRepository.findAll();
    }
}
