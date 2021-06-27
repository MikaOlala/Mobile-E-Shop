package kz.springboot.springtask7.controllers;

import kz.springboot.springtask7.entities.*;
import kz.springboot.springtask7.services.impl.ItemService;
import kz.springboot.springtask7.services.impl.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.header.writers.CacheControlHeadersWriter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private HttpSession session;

    @Value("${file.avatar.viewPath}")
    private String viewPath;

    @Value("${file.avatar.uploadPath}")
    private String uploadPath;

    @Value("${file.avatar.defaultPicture}")
    private String defaultPicture;

    @GetMapping(value = "/")
    public String index(Model model) {
        model.addAttribute("currentUser", getUserData());

        List<Item> items = itemService.getItems();
        model.addAttribute("items", items);

        List<Brand> brands = itemService.getBrands();
        model.addAttribute("brands", brands);

        List<Category> categories = itemService.getCategories();
        model.addAttribute("categories", categories);

        return "index";
    }

    @GetMapping(value = "/admin")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String admin(Model model) {
        model.addAttribute("currentUser", getUserData());

        List<Item> items = itemService.getItems();
        model.addAttribute("items", items);

        List<Brand> brands = itemService.getBrands();
        model.addAttribute("brands", brands);

        return "admin";
    }
    @GetMapping(value = "/countries")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String countries(Model model) {
        model.addAttribute("currentUser", getUserData());

        List<Country> countries = itemService.getCountries();
        model.addAttribute("countries", countries);

        return "countries";
    }
    @GetMapping(value = "/brands")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String brands(Model model) {
        model.addAttribute("currentUser", getUserData());

        List<Brand> brands = itemService.getBrands();
        model.addAttribute("brands", brands);

        List<Country> countries = itemService.getCountries();
        model.addAttribute("countries", countries);

        return "brands";
    }
    @GetMapping(value = "/categories")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String categories(Model model) {
        model.addAttribute("currentUser", getUserData());

        List<Category> categories = itemService.getCategories();
        model.addAttribute("categories", categories);

        return "categories";
    }
    @GetMapping(value = "/users")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String users(Model model) {
        model.addAttribute("currentUser", getUserData());

        List<Users> users = userService.getUsers();
        model.addAttribute("users", users);

        return "users";
    }
    @GetMapping(value = "/roles")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String roles(Model model) {
        model.addAttribute("currentUser", getUserData());

        List<Role> roles = userService.getRoles();
        model.addAttribute("roles", roles);

        return "roles";
    }
    @GetMapping(value = "/pictures")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String pictures(Model model) {
        model.addAttribute("currentUser", getUserData());

        List<Picture> pictures = itemService.getPictures();
        model.addAttribute("pictures", pictures);

        return "pictures";
    }
    @GetMapping(value = "/basket")
    @PreAuthorize("isAuthenticated()")
    public String basket(Model model) {
        model.addAttribute("currentUser", getUserData());

        List<Basket> basket = (List<Basket>)session.getAttribute("BASKET");
        if(basket!=null) {
            int total = 0;
            for (Basket b : basket) {
                if(b.getAmount()!=0) {
                    total = total + b.getPrice();
                }
            }
            model.addAttribute("basket", basket);
            model.addAttribute("total", total);
        }
        else
            model.addAttribute("total", 0);

        return "basket";
    }

    @PostMapping(value = "/basket")
    @PreAuthorize("isAuthenticated()")
    public String addToBasket(@RequestParam(name = "id") Long id) {
        Item item = itemService.getItem(id);

        List<Basket> basket = (List<Basket>)session.getAttribute("BASKET");
        Basket newB = new Basket();
        newB.setAmount(1);
        newB.setName(item.getName());
        newB.setPrice(item.getPrice());

        if(basket!=null) {
            boolean found = false;
            for (Basket b : basket) {
                if(b.getName().equals(item.getName())) {
                    b.setAmount(b.getAmount() + 1);
                    found = true;
                }
            }
            if(!(found)) {
                basket.add(newB);
            }
            session.setAttribute("BASKET", basket);
        }
        else {
            List<Basket> newBasket = new ArrayList<>();
            newBasket.add(newB);
            session.setAttribute("BASKET", newBasket);
        }
        return "redirect:/basket";
    }
    @PostMapping(value = "/plusMinusBasket")
    @PreAuthorize("isAuthenticated()")
    public String plusMinusBasket(@RequestParam(name = "act") String act,
                                  @RequestParam(name = "name") String name) {
        List<Basket> basket = (List<Basket>)session.getAttribute("BASKET");

        for (Basket b : basket) {
            if(b.getName().equals(name)) {
                if (act.equals("plus")) {
                    b.setPrice(b.getPrice()+(b.getPrice()/b.getAmount()));
                    b.setAmount(b.getAmount() + 1);
                }
                else {
                    if(b.getAmount()>0) {
                        b.setAmount(b.getAmount() - 1);
                        b.setPrice(b.getPrice()-(b.getPrice()/b.getAmount()));
                    }
                }
            }
        }

        session.setAttribute("BASKET", basket);

        return "redirect:/basket";
    }
    @PostMapping(value = "/clearPay")
    @PreAuthorize("isAuthenticated()")
    public String clearPay(@RequestParam(name = "clearPay") String act) {
        List<Basket> basket = (List<Basket>)session.getAttribute("BASKET");

        if(act.equals("clear")) {
            basket.clear();
            session.setAttribute("BASKET", basket);
        }
        else {

        }
        return "redirect:/basket";
    }

    @PostMapping(value = "/pay")
    @PreAuthorize("isAuthenticated()")
    public String pay() {
        List<Basket> basket = (List<Basket>)session.getAttribute("BASKET");
        Date date = new Date();
        for (Basket b : basket) {
            b.setDate(date);
            itemService.addBasket(b);
        }

        basket.clear();
        session.setAttribute("BASKET", basket);

        return "redirect:/basket";
    }

    @PostMapping(value = "/addItem")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addItem(@RequestParam(name = "name", defaultValue = "No Task") String name,
                          @RequestParam(name = "description", defaultValue = "No description") String description,
                          @RequestParam(name = "price") int price,
                          @RequestParam(name = "stars") int stars,
                          @RequestParam(name = "picture") String picture,
                          @RequestParam(name = "pictureL") String pictureL,
                          @RequestParam(name = "top") boolean top,
                          @RequestParam(name = "brandId") Long id){
        Brand brand = itemService.getBrand(id);
        if(brand!=null) {
            Date date = new Date();

            Item item = new Item();
            item.setName(name);
            item.setDescription(description);
            item.setPrice(price);
            item.setStars(stars);
            item.setPicture(picture);
            item.setPictureL(pictureL);
            item.setTop(top);
            item.setBrand(brand);
            item.setAdded(date);
            itemService.addItem(item);
        }

        return "redirect:/admin";
    }

    @PostMapping(value = "/addCountry")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addCountry(@RequestParam(name = "name", defaultValue = "No Name") String name,
                             @RequestParam(name = "code", defaultValue = "NNN") String code) {
        Country country = new Country();
        country.setName(name);
        country.setCode(code);
        itemService.addCountry(country);
        return "redirect:/countries";
    }

    @PostMapping(value = "/addBrand")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addBrand(@RequestParam(name = "name", defaultValue = "No Name") String name,
                             @RequestParam(name = "country") Long id) {
        Brand brand = new Brand();
        Country country = itemService.getCountry(id);
        brand.setName(name);
        brand.setCountry(country);
        itemService.addBrand(brand);
        return "redirect:/brands";
    }

    @PostMapping(value = "/addCategory")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addCategory(@RequestParam(name = "name", defaultValue = "No Name") String name,
                              @RequestParam(name = "logo") String logo) {
        Category category = new Category();
        category.setName(name);
        category.setLogoURL(logo);
        itemService.addCategory(category);
        return "redirect:/categories";
    }
    @PostMapping(value = "/addUser")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addUser(@RequestParam(name = "name", defaultValue = "No Name") String name,
                          @RequestParam(name = "email") String email,
                          @RequestParam(name = "password") String password,
                          @RequestParam(name = "repassword") String repassword,
                          @RequestParam(name = "moderator", defaultValue = "false") boolean moderator,
                          @RequestParam(name = "admin", defaultValue = "false") boolean admin) {
        Users user = new Users();
        if (password.equals(repassword)) {
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            System.out.println("Controller - " + moderator + admin);
            if(userService.getUsersByEmail(email)==null) {
                userService.createUserByAdmin(user, moderator, admin);
            }
        }
        return "redirect:/users";
    }
    @PostMapping(value = "/addRole")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addRole(@RequestParam(name = "role", defaultValue = "No_Name") String name) {
        Role role = new Role();
        role.setRole(name);
        userService.addRole(role);
        return "redirect:/roles";
    }
    @PostMapping(value = "/addPicture")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addPicture(@RequestParam(name = "url") MultipartFile file) {
        if(file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")) {
            Picture picture = new Picture();
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy'T'HH:mm:ssZZZZZ");
                Date date = new Date();
                picture.setDate(date);

                String picName = DigestUtils.sha1Hex(formatter.format(date));

                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath + picName+".jpg");
                Files.write(path, bytes);

                picture.setUrl(picName);
                itemService.savePicture(picture);
                return "redirect:/pictures";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:/pictures";
    }
    @PostMapping(value = "/addComment")
    @PreAuthorize("isAuthenticated()")
    public String addComment(@RequestParam(name = "id") Long id,
                             @RequestParam(name = "comment") String commentString) {
        if (commentString!=null) {
            Users user = getUserData();
            Date date = new Date();

            Comment comment = new Comment(0L, commentString, date, id, user);
            userService.addComment(comment);
        }
        return "redirect:/details/" + id + "#commentArea";
    }


    @PostMapping(value = "/editItem")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String editItem(@RequestParam(name = "id") Long id,
                           @RequestParam(name = "name", defaultValue = "No Name") String name,
                           @RequestParam(name = "description", defaultValue = "No Description") String description,
                           @RequestParam(name = "price") int price,
                           @RequestParam(name = "stars") int stars,
                           @RequestParam(name = "picture") String picture,
                           @RequestParam(name = "pictureL") String pictureL,
                           @RequestParam(name = "top") boolean top,
                           @RequestParam(name = "brandId") Long brandId,
                           @RequestParam(name = "act") String act){
        Brand brand = itemService.getBrand(brandId);
        Item item = itemService.getItem(id);
        if(item!=null) {
            if(act.equals("edit")) {
                item.setName(name);
                item.setDescription(description);
                item.setPrice(price);
                item.setStars(stars);
                item.setPicture(picture);
                item.setPictureL(pictureL);
                item.setTop(top);
                item.setBrand(brand);
                itemService.editItem(item);
            }
            else if (act.equals("delete")) {
                itemService.deleteItem(item);
            }
        }
        return "redirect:/admin";
    }
    @PostMapping(value = "/editCountry")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String editCountry(@RequestParam(name = "id") Long id,
                              @RequestParam(name = "name") String name,
                              @RequestParam(name = "code") String code,
                              @RequestParam(name = "act") String act) {
        Country country = itemService.getCountry(id);
        if(country!=null) {
            if(act.equals("edit")) {
                country.setName(name);
                country.setCode(code);
                itemService.saveCountry(country);
            }
            else if (act.equals("delete")) {
                itemService.deleteCountry(country);
            }
        }
        return "redirect:/countries";
    }
    @PostMapping(value = "/editBrand")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String editBrand(@RequestParam(name = "id") Long id,
                              @RequestParam(name = "name") String name,
                              @RequestParam(name = "country") Long countryId,
                              @RequestParam(name = "act") String act) {
        Brand brand = itemService.getBrand(id);
        if(brand!=null) {
            if(act.equals("edit")) {
                Country country = itemService.getCountry(countryId);
                brand.setName(name);
                brand.setCountry(country);
                itemService.saveBrand(brand);
            }
            else if (act.equals("delete")) {
                itemService.deleteBrand(brand);
            }
        }
        return "redirect:/brands";
    }
    @PostMapping(value = "/editCategory")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String editCategory(@RequestParam(name = "id") Long id,
                            @RequestParam(name = "name") String name,
                            @RequestParam(name = "logo") String logo,
                            @RequestParam(name = "act") String act) {
        Category category = itemService.getCategory(id);
        if(category!=null) {
            if(act.equals("edit")) {
                category.setName(name);
                category.setLogoURL(logo);
                itemService.saveCategory(category);
            }
            else if (act.equals("delete")) {
                itemService.deleteCategory(category);
            }
        }
        return "redirect:/categories";
    }
    @PostMapping(value = "/editUser")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String editUser(@RequestParam(name = "id") Long id,
                               @RequestParam(name = "name") String name,
                               @RequestParam(name = "email") String email,
                               @RequestParam(name = "moderator", defaultValue = "false") boolean moderator,
                               @RequestParam(name = "admin", defaultValue = "false") boolean admin,
                               @RequestParam(name = "act") String act) {
        Users user = userService.getUser(id);
        if(act.equals("edit")) {
            user.setName(name);
            user.setEmail(email);
            userService.editUser(user, moderator, admin);
        }

        else if(act.equals("delete")) {
            userService.deleteUser(user);
        }
        return "redirect:/users";
    }
    @PostMapping(value = "/editPassword")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String editPassword(@RequestParam(name = "id") Long id,
                           @RequestParam(name = "new") String nw,
                           @RequestParam(name = "reNew") String reNew) {
        Users user = userService.getUser(id);
        if(nw.equals(reNew)) {
            userService.changePassword(user, nw);
        }
        return "redirect:/users";
    }
    @PostMapping(value = "/editRole")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String editRole(@RequestParam(name = "id") Long id,
                               @RequestParam(name = "role") String roleName,
                               @RequestParam(name = "act") String act) {
        Role role = userService.getRole(id);
        if(role!=null) {
            if(act.equals("edit")) {
                role.setRole(roleName);
                userService.editRole(role);
            }
            else if (act.equals("delete")) {
                userService.deleteRole(role);
            }
        }
        return "redirect:/roles";
    }
    @PostMapping(value = "/deletePicture")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String editPicture(@RequestParam(name = "id") Long id) {
        Picture picture = itemService.getPicture(id);
        if(picture!=null)
            itemService.deletePicture(picture);
        return "redirect:/pictures";
    }

    @PostMapping(value = "/deleteComment")
    @PreAuthorize("isAuthenticated()")
    public String deleteComment(@RequestParam(name = "commentId") Long commentId) {
        Comment comment = userService.getComment(commentId);
        userService.deleteComment(comment);

        return "redirect:/details/" + comment.getItemId() + "#commentArea";
    }
    @PostMapping(value = "/editComment")
    @PreAuthorize("isAuthenticated()")
    public String editComment(@RequestParam(name = "id") Long id,
                              @RequestParam(name = "commentString") String commentString) {
        Comment comment = userService.getComment(id);
        comment.setComment(commentString);
        userService.editComment(comment);

        return "redirect:/details/" + comment.getItemId() + "#commentArea";
    }

    @GetMapping(value = "/detailed")
    public String searchItems(Model model, @RequestParam(name = "name") String name,
                                            @RequestParam(name = "from", defaultValue = "0") int priceFrom,
                                            @RequestParam(name = "to", defaultValue = "0") int priceTo,
                                            @CookieValue(name = "options", defaultValue = "asc") String options,
                                            @RequestParam(name = "brand", defaultValue = "0") Long brandId){
        model.addAttribute("currentUser", getUserData());

        List<Item> items = null;
        List<Brand> brands = itemService.getBrands();
        List<Category> categories = itemService.getCategories();

        if(priceFrom == 0 && priceTo == 0) {
            if(brandId == 0) {
                if (options.equals("asc")) {
                    items = itemService.getItemsBy("%" + name + "%");
                } else if (options.equals("desc")) {
                    items = itemService.getItemsByDesc("%" + name + "%");
                }
            }
            else {
                if (options.equals("asc")) {
                    items = itemService.getItemsByBrand("%" + name + "%", brandId);
                } else if (options.equals("desc")) {
                    items = itemService.getItemsByBrandDesc("%" + name + "%", brandId);
                }
            }
        }
        else if(priceFrom!=0 && priceTo==0){
            if(brandId == 0) {
                if (options.equals("asc")) {
                    items = itemService.getItemsByPrice("%" + name + "%", priceFrom, 9999);
                } else if (options.equals("desc")) {
                    items = itemService.getItemsByPriceDesc("%" + name + "%", priceFrom, 9999);
                }
            }
            else {
                if (options.equals("asc")) {
                    items = itemService.getItemsByBrandAndPrice("%" + name + "%", brandId, priceFrom, 9999);
                } else if (options.equals("desc")) {
                    items = itemService.getItemsByBrandAndPriceDesc("%" + name + "%", brandId, priceFrom, 9999);
                }
            }
        }
        else {
            if(brandId == 0) {
                if (options.equals("asc"))
                    items = itemService.getItemsByPrice("%" + name + "%", priceFrom, priceTo);
                else if (options.equals("desc"))
                    items = itemService.getItemsByPriceDesc("%" + name + "%", priceFrom, priceTo);
            }
            else {
                if (options.equals("asc"))
                    items = itemService.getItemsByBrandAndPrice("%" + name + "%", brandId, priceFrom, priceTo);
                else if (options.equals("desc"))
                    items = itemService.getItemsByBrandAndPriceDesc("%" + name + "%", brandId, priceFrom, priceTo);
            }
        }
        model.addAttribute("items", items);
        model.addAttribute("brands", brands);
        model.addAttribute("categories", categories);
        return "detailed";
    }

    @PostMapping(value = "/options")
    public String options (HttpServletResponse response,
                           @RequestParam(name = "options", defaultValue = "acs") String options) {
        Cookie cookie = new Cookie("options", options);
        try{
            response.addCookie(cookie);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/detailed?name=";
    }

    @GetMapping(value = "details/{id}")
    public String details(Model model, @PathVariable(name = "id") Long id){
        model.addAttribute("currentUser", getUserData());
        boolean moderator = false;
        if(getUserData()!=null) {
            for (Role r : getUserData().getRoles()) {
                if(r.getRole().equals("ROLE_MODERATOR"))
                    moderator = true;
            }
        }
        model.addAttribute("isModerator", moderator);

        Item item = itemService.getItem(id);
        model.addAttribute("item", item);

        List<Brand> brands = itemService.getBrands();
        model.addAttribute("brands", brands);

        List<Category> categories = itemService.getCategories();
        model.addAttribute("categories", categories);

        List<Picture> pictures = itemService.getPicturesByItem(id);
        model.addAttribute("pictures", pictures);

        List<Comment> comments = userService.getCommentByItem(id);
        model.addAttribute("comments", comments);

        return "details";
    }

    @GetMapping(value = "detailsAdmin/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String detailsAdmin(Model model, @PathVariable(name = "id") Long id){
        model.addAttribute("currentUser", getUserData());

        Item item = itemService.getItem(id);
        model.addAttribute("item", item);

        List<Brand> brands = itemService.getBrands();
        model.addAttribute("brands", brands);

        List<Category> itsCategories = item.getCategories();
        model.addAttribute("itsCategories", itsCategories);

        List<Category> categories = itemService.getCategories();
        model.addAttribute("categories", categories);

        List<Picture> pictures = itemService.getPictureByNullItem();
        model.addAttribute("pictures", pictures);

        List<Picture> itsPictures = itemService.getPicturesByItem(id);
        model.addAttribute("itsPictures", itsPictures);

        return "detailsAdmin";
    }

    @GetMapping(value = "detailsCountry/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String detailsCountry(Model model, @PathVariable(name = "id") Long id){
        model.addAttribute("currentUser", getUserData());

        Country country = itemService.getCountry(id);
        model.addAttribute("country", country);
        return "detailsCountry";
    }
    @GetMapping(value = "detailsBrand/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String detailsBrand(Model model, @PathVariable(name = "id") Long id){
        model.addAttribute("currentUser", getUserData());

        Brand brand = itemService.getBrand(id);
        model.addAttribute("brand", brand);

        List<Country> countries = itemService.getCountries();
        model.addAttribute("countries", countries);
        return "detailsBrand";
    }
    @GetMapping(value = "detailsCategory/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String detailsCategory(Model model, @PathVariable(name = "id") Long id){
        model.addAttribute("currentUser", getUserData());

        Category category = itemService.getCategory(id);
        model.addAttribute("category", category);
        return "detailsCategory";
    }
    @GetMapping(value = "detailsUser/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String detailsUser(Model model, @PathVariable(name = "id") Long id){
        model.addAttribute("currentUser", getUserData());

        Users user = userService.getUser(id);
        model.addAttribute("user", user);
        return "detailsUser";
    }
    @GetMapping(value = "detailsRole/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String detailsRole(Model model, @PathVariable(name = "id") Long id){
        model.addAttribute("currentUser", getUserData());

        Role role = userService.getRole(id);
        model.addAttribute("role", role);
        return "detailsRole";
    }

    @PostMapping(value = "/assign")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String assign(@RequestParam(name = "itemId") Long itemId,
                         @RequestParam(name = "categoryId") Long catId) {
        Category category = itemService.getCategory(catId);
        Item item = itemService.getItem(itemId);
        if(category!=null && item!=null) {
            List<Category> categories = item.getCategories();
            if(categories==null) {
                categories = new ArrayList<>();
            }
            else {
                for (Category c : categories) {
                    if (c.getId().equals(category.getId()))
                        return "redirect:/detailsAdmin/" + itemId;
                }
            }
            categories.add(category);
            itemService.editItem(item);
            return "redirect:/detailsAdmin/" + itemId;
        }
        return "redirect:/";
    }

    @PostMapping(value = "/minus")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String minus(@RequestParam(name = "itemId") Long itemId,
                        @RequestParam(name = "categoryId") Long catId) {
        Category category = itemService.getCategory(catId);
        Item item = itemService.getItem(itemId);
        if(category!=null && item!=null) {
            List<Category> categories = item.getCategories();
            for (Category c : categories) {
                if (c.getId().equals(category.getId())) {
                    categories.remove(category);
                    itemService.editItem(item);
                    return "redirect:/detailsAdmin/" + itemId;
                }
            }
            return "redirect:/detailsAdmin/" + itemId;
        }
        return "redirect:/";
    }

    @PostMapping(value = "/assignPic")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String assignPic(@RequestParam(name = "itemId") Long itemId,
                            @RequestParam(name = "picId") Long picId) {
        Picture picture = itemService.getPicture(picId);
        Item item = itemService.getItem(itemId);
        if(picture!=null && item!=null) {
            if(picture.getItem()==null) {
                picture.setItem(item);
                itemService.savePicture(picture);
            }
            return "redirect:/detailsAdmin/" + itemId + "#picDiv";
        }
        return "redirect:/detailsAdmin/" + itemId + "#picDiv";
    }

    @PostMapping(value = "/minusPic")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String minusPic(@RequestParam(name = "itemId") Long itemId,
                            @RequestParam(name = "picId") Long picId) {
        Picture picture = itemService.getPicture(picId);
        Item item = itemService.getItem(itemId);
        if(picture!=null && item!=null) {
            if(picture.getItem()!=null) {
                picture.setItem(null);
                itemService.savePicture(picture);
            }
            return "redirect:/detailsAdmin/" + itemId + "#picDiv";
        }
        return "redirect:/detailsAdmin/" + itemId + "#picDiv";
    }

    @GetMapping(value = "/403")
    public String accessDenied(Model model) {
        model.addAttribute("currentUser", getUserData());
        return "403";
    }

    @GetMapping(value = "/login")
    @PreAuthorize(value = "isAnonymous()")
    public String login(Model model){
        model.addAttribute("currentUser", getUserData());
        return "login";
    }

    @GetMapping(value = "/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile(Model model) {
        model.addAttribute("currentUser", getUserData());
        return "profile";
    }

    @GetMapping(value = "/reg")
    @PreAuthorize("isAnonymous()")
    public String reg() {
        return "reg";
    }

    @PostMapping(value = "/reg")
    @PreAuthorize("isAnonymous()")
    public String reg(Model model, @RequestParam(name = "email") String email,
                                    @RequestParam(name = "password") String password,
                                    @RequestParam(name = "repassword") String repassword,
                                    @RequestParam(name = "name") String name) {
        if(password.equals(repassword)) {
            if(userService.getUsersByEmail(email)==null) {
                Users user = new Users();
                user.setEmail(email);
                user.setPassword(password);
                user.setName(name);

                userService.createUser(user);
                return "redirect:/login";
            }
        }
        else
            return "redirect:/reg?error";
        return "redirect:/reg?error";
    }

    @PostMapping(value = "/changeName")
    @PreAuthorize("isAuthenticated()")
    public String changeName(@RequestParam(name = "name", required = false) String name) {
        Users user = getUserData();

        user.setName(name);
        userService.editUser(user);
        return "redirect:/profile";
    }

    @PostMapping(value = "/changePassword")
    @PreAuthorize("isAuthenticated()")
    public String changePassword(@RequestParam(name = "old") String old,
                                 @RequestParam(name = "nw") String nw,
                                 @RequestParam(name = "reNew") String reNew) {
        if (nw.equals(reNew)) {
            if (userService.diffPasswords(getUserData(), old, nw)!=null)
                return "redirect:/profile?success";
            else
                return "redirect:/profile?error";
        }
        else
            return "redirect:/profile?error";
    }

    @PostMapping(value = "/avatar")
    @PreAuthorize("isAuthenticated()")
    public String avatar(@RequestParam(name = "ava") MultipartFile ava) {
        if(ava.getContentType().equals("image/jpeg") || ava.getContentType().equals("image/png")) {
            try {
                Users user = getUserData();

                String picName = DigestUtils.sha1Hex("avatar_"+user.getId()+"_!Picture");

                byte[] bytes = ava.getBytes();
                Path path = Paths.get(uploadPath + picName+".jpg");
                Files.write(path, bytes);

                user.setPicture(picName);
                userService.editUser(user);
                return "redirect:/profile";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:/profile";
    }

    @GetMapping(value = "/viewPhoto/{url}", produces = {MediaType.IMAGE_JPEG_VALUE})
    public @ResponseBody byte[] viewProfilePhoto(@PathVariable(name = "url") String url) throws IOException {
        String pictureURL = viewPath+defaultPicture;
        if(url!=null) {
            pictureURL = viewPath+url+".jpg";
        }
        InputStream in;
        try {
            ClassPathResource resource = new ClassPathResource(pictureURL);
            in = resource.getInputStream();
        }catch (Exception e) {
            ClassPathResource resource = new ClassPathResource(viewPath+defaultPicture);
            in = resource.getInputStream();
            e.printStackTrace();
        }
        return IOUtils.toByteArray(in);
    }
//    @GetMapping(value = "/viewPicture/{url}", produces = {MediaType.IMAGE_JPEG_VALUE})
//    @PreAuthorize("isAuthenticated()")
//    public @ResponseBody byte[] viewPicture(@PathVariable(name = "url") String url) throws IOException {
//        String pictureURL = viewPath+url+".jpg";
//
//        InputStream in;
//        try {
//            ClassPathResource resource = new ClassPathResource(pictureURL);
//            in = resource.getInputStream();
//        }catch (Exception e) {
//            ClassPathResource resource = new ClassPathResource(viewPath+defaultPicture);
//            in = resource.getInputStream();
//            e.printStackTrace();
//        }
//        return IOUtils.toByteArray(in);
//    }

    private Users getUserData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User secUser = (User)authentication.getPrincipal();
            Users myUser = userService.getUsersByEmail(secUser.getUsername());
            return myUser;
        }
        return null;
    }
}