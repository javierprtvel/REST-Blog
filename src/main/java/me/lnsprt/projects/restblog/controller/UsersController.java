package me.lnsprt.projects.restblog.controller;

import io.swagger.annotations.*;
import me.lnsprt.projects.restblog.model.Subscription;
import me.lnsprt.projects.restblog.model.User;
import me.lnsprt.projects.restblog.repository.SubscriptionRepository;
import me.lnsprt.projects.restblog.repository.UserRepository;
import me.lnsprt.projects.restblog.repository.query.SubscriptionQueryInterpreter;
import me.lnsprt.projects.restblog.repository.query.UserQueryInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RelProvider;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Api(
        tags = "Users",
        description = "User and subscription management",
        produces = "application/json, application/xml",
        consumes = "application/json, application/xml"
)
public class UsersController {
    private UserRepository userdb;
    private SubscriptionRepository subscriptiondb;
    private PasswordEncoder passwordEncoder;
    private UserQueryInterpreter userqi;
    private SubscriptionQueryInterpreter subscriptionqi;
    private RelProvider relProvider;

    @Autowired
    public UsersController(UserRepository userdb, SubscriptionRepository subscriptiondb, PasswordEncoder passwordEncoder,
            UserQueryInterpreter userqi, SubscriptionQueryInterpreter subscriptionqi,
                       RelProvider relProvider) {
        this.userdb = userdb;
        this.subscriptiondb = subscriptiondb;
        this.passwordEncoder = passwordEncoder;
        this.userqi = userqi;
        this.subscriptionqi = subscriptionqi;
        this.relProvider = relProvider;
    }


    @ApiOperation("Get users list")
    @ApiResponses({
            @ApiResponse(code=200, message = "The list of users", response = User.class, responseContainer = "List", responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            }),
            @ApiResponse(code=401, message = "Only logged users can access this resource"),
            @ApiResponse(code=403, message = "Only users with role ADMIN can access this resource")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(
            produces = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    @ResponseStatus(HttpStatus.OK)
    public  ResponseEntity<Page<User>> getUsers(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                   @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                   @RequestParam(value= "sort", defaultValue = "signupDate") String sortProperty,
                                                   @RequestParam(value= "sortDir", defaultValue = "desc") String sortDir) {
        Page<User> users = userdb.findAll(PageRequest.of(page, size, new Sort(Sort.Direction.fromString(sortDir), sortProperty)));

        ResponseEntity.BodyBuilder response = ResponseEntity.ok();

        Link self = ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(UsersController.class).getUsers(page, size, sortProperty, sortDir))
                .withSelfRel();
        response.header(HttpHeaders.LINK, self.toString());

        if(!users.isFirst()) {
            Link first = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(UsersController.class).getUsers(0, size, sortProperty, sortDir))
                    .withRel(Link.REL_FIRST).expand();
            response.header(HttpHeaders.LINK, first.toString());

            Link prev = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(UsersController.class).getUsers(page - 1, size, sortProperty, sortDir))
                    .withRel(Link.REL_PREVIOUS).expand();
            response.header(HttpHeaders.LINK, prev.toString());
        }

        if(!users.isLast()) {
            Link last = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(UsersController.class).getUsers(users.getTotalPages() - 1, size, sortProperty, sortDir))
                    .withRel(Link.REL_LAST).expand();
            response.header(HttpHeaders.LINK, last.toString());

            Link next = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(UsersController.class).getUsers(page + 1, size, sortProperty, sortDir))
                    .withRel(Link.REL_NEXT).expand();
            response.header(HttpHeaders.LINK, next.toString());
        }

        return response.body(users.map(user -> user.setPassword("*******")));
    }


    @ApiOperation("Search users")
    @ApiResponses({
            @ApiResponse(code=200, message = "The list of filtered users", response = User.class, responseContainer = "List", responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            }),
            @ApiResponse(code=400, message = "Search query is syntactically or semantically incorrect"),
            @ApiResponse(code=403, message = "Only queries over editors ('roles like editor') and users with role ADMIN are allowed to access this resource")
    })
    @PreAuthorize("hasRole('ADMIN') or #searchQuery.contains('roles like EDITOR')")
    @GetMapping(
            path = "/search",
            produces = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    @ResponseStatus(HttpStatus.OK)
    public  ResponseEntity<Page<User>> searchUsers(@RequestParam("q") String searchQuery,
                                                   @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                   @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                   @RequestParam(value= "sort", defaultValue = "signupDate") String sortProperty,
                                                   @RequestParam(value= "sortDir", defaultValue = "desc") String sortDir){

        try {
            Page<User> users = userqi.executeQuery(searchQuery, PageRequest.of(page, size, new Sort(Sort.Direction.fromString(sortDir), sortProperty)));

            ResponseEntity.BodyBuilder response = ResponseEntity.ok();

            Link self = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(UsersController.class).searchUsers(searchQuery, page, size, sortProperty, sortDir))
                    .withSelfRel();
            response.header(HttpHeaders.LINK, self.toString());

            if(!users.isFirst()) {
                Link first = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(UsersController.class).searchUsers(searchQuery, 0, size, sortProperty, sortDir))
                        .withRel(Link.REL_FIRST).expand();
                response.header(HttpHeaders.LINK, first.toString());

                Link prev = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(UsersController.class).searchUsers(searchQuery, page - 1, size, sortProperty, sortDir))
                        .withRel(Link.REL_PREVIOUS).expand();
                response.header(HttpHeaders.LINK, prev.toString());
            }

            if(!users.isLast()) {
                Link last = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(UsersController.class).searchUsers(searchQuery, users.getTotalPages() - 1, size, sortProperty, sortDir))
                        .withRel(Link.REL_LAST).expand();
                response.header(HttpHeaders.LINK, last.toString());

                Link next = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(UsersController.class).searchUsers(searchQuery, page + 1, size, sortProperty, sortDir))
                        .withRel(Link.REL_NEXT).expand();
                response.header(HttpHeaders.LINK, next.toString());
            }


            return response.body(users.map(user -> user.setPassword("*******")));
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @ApiOperation("Get a user")
    @ApiResponses({
            @ApiResponse(code=200, message = "The user", response = User.class, responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            }),
            @ApiResponse(code=401, message = "Only logged users can access this resource"),
            @ApiResponse(code=404, message = "User not found")
    })
    @PreAuthorize("hasRole('READER')")
    @GetMapping(
            path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> getUser(@PathVariable("id")String id) {
        if(userdb.existsById(id)) {
            User u = userdb.findById(id);
            Link self = ControllerLinkBuilder.linkTo(UsersController.class).slash(id).withSelfRel();
            Link collection = ControllerLinkBuilder.linkTo(UsersController.class).withRel(relProvider.getCollectionResourceRelFor(User.class));
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.LINK, self.toString())
                    .header(HttpHeaders.LINK, collection.toString())
                    .body(u.setPassword("*******"));
        }
        else
            return ResponseEntity.notFound().build();
    }

    @ApiOperation("Get a user by its nickname")
    @ApiResponses({
            @ApiResponse(code=200, message = "The user", response = User.class, responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            }),
            @ApiResponse(code=401, message = "Only logged users can access this resource"),
            @ApiResponse(code=403, message = "Users without ADMIN role can only access its own user resource"),
            @ApiResponse(code=404, message = "User not found")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('READER') and principal == #nickname")
    @GetMapping(
            path = "/nickname/{nickname}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> getUserByNickname(@PathVariable("nickname")String nickname) {
        if(userdb.existsByNickname(nickname)) {
            Link self = ControllerLinkBuilder.linkTo(UsersController.class).slash("/nickname").slash(nickname).withSelfRel();
            Link collection = ControllerLinkBuilder.linkTo(UsersController.class).withRel(relProvider.getCollectionResourceRelFor(User.class));
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.LINK, self.toString())
                    .header(HttpHeaders.LINK, collection.toString())
                    .body(userdb.findByNickname(nickname).setPassword("*******"));
        }

        else
            return ResponseEntity.notFound().build();
    }

    @ApiOperation("Get editors list")
    @ApiResponses({
            @ApiResponse(code=200, message = "The list of editors", response = User.class, responseContainer = "List", responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            })
    })
    @PreAuthorize("permitAll()")
    @GetMapping(
            path = "/editors",
            produces = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<User>> getEditors(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                 @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                 @RequestParam(value= "sort", defaultValue = "signupDate") String sortProperty,
                                                 @RequestParam(value= "sortDir", defaultValue = "desc") String sortDir){
        Page<User> editors = userdb.findByRolesContains("EDITOR", PageRequest.of(page, size, new Sort(Sort.Direction.fromString(sortDir), sortProperty)));

        ResponseEntity.BodyBuilder response = ResponseEntity.ok();

        Link self = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(UsersController.class).getEditors(page, size, sortProperty, sortDir))
                .withSelfRel();
        response.header(HttpHeaders.LINK, self.toString());

        if(!editors.isFirst()) {
            Link first = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(UsersController.class).getEditors(0, size, sortProperty, sortDir))
                    .withRel(Link.REL_FIRST).expand();
            response.header(HttpHeaders.LINK, first.toString());

            Link prev = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(UsersController.class).getEditors(page - 1, size, sortProperty, sortDir))
                    .withRel(Link.REL_PREVIOUS).expand();
            response.header(HttpHeaders.LINK, prev.toString());
        }

        if(!editors.isLast()) {
            Link last = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(UsersController.class).getEditors(editors.getTotalPages() - 1, size, sortProperty, sortDir))
                    .withRel(Link.REL_LAST).expand();
            response.header(HttpHeaders.LINK, last.toString());

            Link next = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(UsersController.class).getEditors(page + 1, size, sortProperty, sortDir))
                    .withRel(Link.REL_NEXT).expand();
            response.header(HttpHeaders.LINK, next.toString());
        }


        return response.body(editors.map(user -> user.setPassword("*******")));
    }

    @ApiOperation("Get a editor")
    @ApiResponses({
            @ApiResponse(code=200, message = "The editor", response = User.class, responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            }),
            @ApiResponse(code=404, message = "Editor not found")
    })
    @PreAuthorize("permitAll()")
    @GetMapping(
            path = "/editors/{id}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> getEditor(@PathVariable("id")String id) {

        if(userdb.existsById(id)) {
            User u = userdb.findById(id);

            if(userdb.findByRoles("EDITOR").contains(u)) {
                Link self = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(UsersController.class).getEditor(id))
                        .withSelfRel();
                Link collection = ControllerLinkBuilder.linkTo(UsersController.class).slash("editors").withRel("EditorList");
                return ResponseEntity
                        .ok()
                        .header(HttpHeaders.LINK, self.toString())
                        .header(HttpHeaders.LINK, collection.toString())
                        .body(u.setPassword("*******"));
            }
            else
                return ResponseEntity.notFound().build();
        }
        else
            return ResponseEntity.notFound().build();
    }

    @ApiOperation("Get a editor by its nickname")
    @ApiResponses({
            @ApiResponse(code=200, message = "The editor", response = User.class, responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            }),
            @ApiResponse(code=404, message = "Editor not found")
    })
    @PreAuthorize("permitAll()")
    @GetMapping(
            path = "/editors/nickname/{nickname}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> getEditorByNickname(@PathVariable("nickname")String nickname) {

        if(userdb.existsByNickname(nickname)) {
            User u = userdb.findByNickname(nickname);

            if(userdb.findByRoles("EDITOR").contains(u)) {
                Link self = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(UsersController.class).getEditorByNickname(nickname))
                        .withSelfRel();
                Link collection = ControllerLinkBuilder.linkTo(UsersController.class).slash("editors").withRel("EditorList");
                return ResponseEntity
                        .ok()
                        .header(HttpHeaders.LINK, self.toString())
                        .header(HttpHeaders.LINK, collection.toString())
                        .body(u.setPassword("*******"));
            }
            else
                return ResponseEntity.notFound().build();
        }
        else
            return ResponseEntity.notFound().build();
    }

    @ApiOperation("Create a user")
    @ApiResponses({
            @ApiResponse(code=409, message = "User already exists"),
            @ApiResponse(code=201, message = "User created correctly", responseHeaders = {
                    @ResponseHeader(name = "Location", description = "The location where the created user can be found", response = URI.class),
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class, responseContainer = "List")
            })
    })
    @PreAuthorize("permitAll()")
    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> createUser(@RequestBody User newUser) {
        if(userdb.existsByNickname(newUser.getNickname()))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        else {
            User user = new User();
            user.setName(newUser.getName());
            user.setPassword(passwordEncoder.encode(newUser.getPassword()));
            user.setNickname(newUser.getNickname());
            user.setEmail(newUser.getEmail());
            user.setSignupDate(new Date());
            user.setRoles(Collections.singletonList("READER")); // new users have role "READER" by default
            user.setSuspended(false);

            user = userdb.save(user);

            Link self = ControllerLinkBuilder.linkTo(UsersController.class).slash(user.getId()).withSelfRel();
            Link collection = ControllerLinkBuilder.linkTo(UsersController.class).withRel(relProvider.getCollectionResourceRelFor(User.class));
            return ResponseEntity
                    .created(URI.create(self.getHref()))
                    .header(HttpHeaders.LINK, self.toString())
                    .header(HttpHeaders.LINK, collection.toString())
                    .build();
        }
    }

    @ApiOperation("Update a existing user state")
    @ApiResponses({
            @ApiResponse(code=401, message = "Only logged users can access this resource"),
            @ApiResponse(code=409, message = "New user data  is not correct"),
            @ApiResponse(code=403, message = "Only users with role ADMIN or MODERATOR can update user state"),
            @ApiResponse(code=404, message = "User not found"),
            @ApiResponse(code=200, message = "User modified correctly")
    })
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @PutMapping(
            path = "/{id}",
            consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity modifyUser(@PathVariable("id") String id, @RequestBody User updatedUser) {

        if(!userdb.existsById(id))
            return ResponseEntity.notFound().build();
        else {
            User currentUser = userdb.findById(id);

            // check if nickname is different and already in use by another user
            String currentNickname = currentUser.getNickname();
            String updatedNickname = updatedUser.getNickname();
            if(!currentNickname.equals(updatedNickname) && userdb.existsByNickname(updatedNickname))
                return ResponseEntity.status(HttpStatus.CONFLICT).build();

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
            if((authorities.contains("ADMIN"))) { // is admin: can change roles AND suspend account
                currentUser.setRoles(updatedUser.getRoles());
                if(!currentUser.isSuspended() == (updatedUser.isSuspended()))
                    currentUser.setSuspended(updatedUser.isSuspended());
            }
            else { // is moderator: can suspend account only
                currentUser.setSuspended(updatedUser.isSuspended());
            }
            // assume that remaining fields are populated with (valid) data
            userdb.save(currentUser);

            return ResponseEntity.ok().build();
        }

    }

    @ApiOperation("Delete a user")
    @ApiResponses({
            @ApiResponse(code=204, message = "User deleted correctly", responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            }),
            @ApiResponse(code=401, message = "Only logged users can access this resource"),
            @ApiResponse(code=403, message = "Only users with role ADMIN or READER (upon own user) can access this resource"),
            @ApiResponse(code=404, message = "User not found")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('READER')")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity deleteUser(@PathVariable("id") String id) {
        Link all = ControllerLinkBuilder.linkTo(UsersController.class).withRel(relProvider.getCollectionResourceRelFor(User.class));

        if(!userdb.existsById(id)) {
            return ResponseEntity
                    .notFound()
                    .header(HttpHeaders.LINK, all.toString())
                    .build();
        }
        else {
            // users without admin access level can only delete their own account
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
            String principal = (String)auth.getPrincipal();
            User u = userdb.findById(id);
            if(!authorities.contains("ADMIN") && !principal.equals(u.getNickname()))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

            userdb.deleteById(id);
            subscriptiondb.deleteByUser(id);

            return ResponseEntity
                    .noContent()
                    .header(HttpHeaders.LINK, all.toString())
                    .build();
        }
    }

    @ApiOperation("Get user subscriptions")
    @ApiResponses({
            @ApiResponse(code=200, message = "The list of subscriptions", response = Subscription.class, responseContainer = "List", responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            }),
            @ApiResponse(code=401, message = "Only logged users can access this resource"),
            @ApiResponse(code=403, message = "Only users with role READER (upon own subscriptions) can access this resource"),
            @ApiResponse(code=404, message = "User not found")
    })
    @PreAuthorize("hasRole('READER')")
    @GetMapping(
            path = "/{id}/subscriptions",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}

    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<Subscription>> getUserSubscriptions(@PathVariable("id") String id,
                                                               @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                               @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                               @RequestParam(value= "sort", defaultValue = "reference") String sortProperty,
                                                               @RequestParam(value= "sortDir", defaultValue = "desc") String sortDir) {

        if(!userdb.existsById(id))
            return ResponseEntity.notFound().build();
        else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String principal = (String)auth.getPrincipal();
            User u = userdb.findById(id);
            if(principal.equals(u.getNickname())) {
                Page<Subscription> subscriptions = subscriptiondb.findByUser(id, PageRequest.of(page, size, new Sort(Sort.Direction.fromString(sortDir), sortProperty)));

                ResponseEntity.BodyBuilder response = ResponseEntity.ok();

                Link self = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(UsersController.class).getUserSubscriptions(id, page, size, sortProperty, sortDir))
                        .withSelfRel();
                response.header(HttpHeaders.LINK, self.toString());

                if(!subscriptions.isFirst()) {
                    Link first = ControllerLinkBuilder.linkTo(
                            ControllerLinkBuilder.methodOn(UsersController.class).getUserSubscriptions(id, 0, size, sortProperty, sortDir))
                            .withRel(Link.REL_FIRST).expand();
                    response.header(HttpHeaders.LINK, first.toString());

                    Link prev = ControllerLinkBuilder.linkTo(
                            ControllerLinkBuilder.methodOn(UsersController.class).getUserSubscriptions(id, page - 1, size, sortProperty, sortDir))
                            .withRel(Link.REL_PREVIOUS).expand();
                    response.header(HttpHeaders.LINK, prev.toString());
                }

                if(!subscriptions.isLast()) {
                    Link last = ControllerLinkBuilder.linkTo(
                            ControllerLinkBuilder.methodOn(UsersController.class).getUserSubscriptions(id, subscriptions.getTotalPages() - 1, size, sortProperty, sortDir))
                            .withRel(Link.REL_LAST).expand();
                    response.header(HttpHeaders.LINK, last.toString());

                    Link next = ControllerLinkBuilder.linkTo(
                            ControllerLinkBuilder.methodOn(UsersController.class).getUserSubscriptions(id, page + 1, size, sortProperty, sortDir))
                            .withRel(Link.REL_NEXT).expand();
                    response.header(HttpHeaders.LINK, next.toString());
                }

                return response.body(subscriptions);
            }
            else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
    }

    @ApiOperation("Search subscriptions")
    @ApiResponses({
            @ApiResponse(code=200, message = "The list of filtered subscriptions", response = Subscription.class, responseContainer = "List", responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            }),
            @ApiResponse(code=400, message = "Search query is syntactically or semantically incorrect"),
            @ApiResponse(code=401, message = "Only logged users can access this resource"),
            @ApiResponse(code=403, message = "Only users with role ADMIN or READER can access this resource")
    })
    @PreAuthorize("hasRole('READER') or hasRole('ADMIN')")
    @GetMapping(
            path = "/subscriptions/search",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}

    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<Subscription>> searchSubscriptions(@RequestParam("q") String searchQuery,
                                                                   @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                   @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                   @RequestParam(value= "sort", defaultValue = "reference") String sortProperty,
                                                                   @RequestParam(value= "sortDir", defaultValue = "desc") String sortDir) {

        try {
            Page<Subscription> subscriptions = subscriptionqi.executeQuery(searchQuery, PageRequest.of(page, size, new Sort(Sort.Direction.fromString(sortDir), sortProperty)));

            ResponseEntity.BodyBuilder response = ResponseEntity.ok();

            Link self = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(UsersController.class).searchSubscriptions(searchQuery, page, size, sortProperty, sortDir))
                    .withSelfRel();
            response.header(HttpHeaders.LINK, self.toString());

            if(!subscriptions.isFirst()) {
                Link first = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(UsersController.class).searchSubscriptions(searchQuery, 0, size, sortProperty, sortDir))
                        .withRel(Link.REL_FIRST).expand();
                response.header(HttpHeaders.LINK, first.toString());

                Link prev = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(UsersController.class).searchSubscriptions(searchQuery, page - 1, size, sortProperty, sortDir))
                        .withRel(Link.REL_PREVIOUS).expand();
                response.header(HttpHeaders.LINK, prev.toString());
            }

            if(!subscriptions.isLast()) {
                Link last = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(UsersController.class).searchSubscriptions(searchQuery, subscriptions.getTotalPages() - 1, size, sortProperty, sortDir))
                        .withRel(Link.REL_LAST).expand();
                response.header(HttpHeaders.LINK, last.toString());

                Link next = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(UsersController.class).searchSubscriptions(searchQuery, page + 1, size, sortProperty, sortDir))
                        .withRel(Link.REL_NEXT).expand();
                response.header(HttpHeaders.LINK, next.toString());
            }

            return response.body(subscriptions);
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @ApiOperation("Get a user subscription")
    @ApiResponses({
            @ApiResponse(code=200, message = "The subscription", response = Subscription.class, responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            }),
            @ApiResponse(code=401, message = "Only logged users can access this resource"),
            @ApiResponse(code=403, message = "User or subscription not found"),
            @ApiResponse(code=404, message = "User or subscription not found"),
            @ApiResponse(code=409, message = "User and subscription are not related")
    })
    @PreAuthorize("hasRole('READER')")
    @GetMapping(
            path = "/{userId}/subscriptions/{id}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Subscription> getSubscription(@PathVariable("userId")String userId, @PathVariable("id") String id) {

        if(userdb.existsById(userId) && subscriptiondb.existsById(id)) {
            Subscription s = subscriptiondb.findById(id);

            if(userId.equals(s.getUser())) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String principal = (String)auth.getPrincipal();
                User u = userdb.findById(userId);
                if(principal.equals(u.getNickname())) {
                    Link self = ControllerLinkBuilder.linkTo(
                            ControllerLinkBuilder.methodOn(UsersController.class).getSubscription(userId, id))
                            .withSelfRel();
                    Link collection = ControllerLinkBuilder.linkTo(UsersController.class)
                            .slash(userId)
                            .slash("subscriptions")
                            .withRel(relProvider.getCollectionResourceRelFor(Subscription.class));
                    return ResponseEntity
                            .ok()
                            .header(HttpHeaders.LINK, self.toString())
                            .header(HttpHeaders.LINK, collection.toString())
                            .body(s);
                }
                else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            else {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }
        else
            return ResponseEntity.notFound().build();
    }

    @ApiOperation("Create a subscription for a user")
    @ApiResponses({
            @ApiResponse(code=401, message = "Only logged users can access this resource"),
            @ApiResponse(code=409, message = "Subscription data is not correct"),
            @ApiResponse(code=403, message = "Only users with role READER can create subscriptions"),
            @ApiResponse(code=404, message = "User not found"),
            @ApiResponse(code=201, message = "Subscription created correctly", responseHeaders = {
                    @ResponseHeader(name = "Location", description = "The location where the created subscription can be found", response = URI.class),
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class, responseContainer = "List")
            })
    })
    @PreAuthorize("hasRole('READER')")
    @PostMapping(
            path = "/{id}/subscriptions",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity createSubscription(@PathVariable("id") String id, @RequestBody Subscription subscription) {

        if(!userdb.existsById(id))
            return ResponseEntity.notFound().build();
        else {
            User u = userdb.findById(id);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String principal = (String)auth.getPrincipal();
            if(!principal.equals(u.getNickname())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            subscription.setUser(id);
            subscription = subscriptiondb.save(subscription);

            Link self = ControllerLinkBuilder.linkTo(UsersController.class)
                    .slash(id)
                    .slash("subscriptions")
                    .slash(subscription.getId()).withSelfRel();
            Link collection = ControllerLinkBuilder.linkTo(UsersController.class)
                    .slash(id)
                    .slash("subscriptions")
                    .withRel(relProvider.getCollectionResourceRelFor(Subscription.class));
            return ResponseEntity
                    .created(URI.create(self.getHref()))
                    .header(HttpHeaders.LINK, self.toString())
                    .header(HttpHeaders.LINK, collection.toString())
                    .build();
        }
    }

    @ApiOperation("Update a existing subscription state")
    @ApiResponses({
            @ApiResponse(code=401, message = "Only logged users can access this resource"),
            @ApiResponse(code=409, message = "New subscription data  is not correct"),
            @ApiResponse(code=403, message = "Only users with role READER (upon subscriptions of its own) can update subscription state"),
            @ApiResponse(code=404, message = "User or subscription not found"),
            @ApiResponse(code=200, message = "Subscription modified correctly")
    })
    @PreAuthorize("hasRole('READER')")
    @PutMapping(
            path = "/{userId}/subscriptions/{id}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity modifySubscription(@PathVariable("userId") String userId, @PathVariable("id") String id,
                                             @RequestBody Subscription updatedSubscription) {

        if(!userdb.existsById(userId) || !subscriptiondb.existsById(id))
            return ResponseEntity.notFound().build();
        else {
            Subscription currentSubscription = subscriptiondb.findById(id);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String principal = (String)auth.getPrincipal();
            User u = userdb.findById(userId);
            if(!principal.equals(u.getNickname())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if(!currentSubscription.getUser().equals(userId))
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            else {
                updatedSubscription.setId(currentSubscription.getId());
                updatedSubscription.setUser(userId);
                subscriptiondb.save(updatedSubscription);

                return ResponseEntity.ok().build();
            }
        }
    }

    @ApiOperation("Delete a subscription")
    @ApiResponses({
            @ApiResponse(code=204, message = "Subscription deleted correctly", responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            }),
            @ApiResponse(code=401, message = "Only logged users can access this resource"),
            @ApiResponse(code=403, message = "Only users with role READER (upon a subscription of its own) can access this resource"),
            @ApiResponse(code=404, message = "User or subscription not found"),
            @ApiResponse(code=409, message = "User and subscription are not related")
    })
    @PreAuthorize("hasRole('READER')")
    @DeleteMapping(path = "/{userId}/subscriptions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity deleteSubscription(@PathVariable("userId") String userId, @PathVariable("id") String id) {
        Link all = ControllerLinkBuilder.linkTo(UsersController.class)
                .slash(userId)
                .slash("subscriptions")
                .withRel(relProvider.getCollectionResourceRelFor(Subscription.class));

        if(!userdb.existsById(userId) || !subscriptiondb.existsById(id))
            return ResponseEntity
                    .notFound()
                    .header(HttpHeaders.LINK, all.toString())
                    .build();
        else {
            Subscription subscription = subscriptiondb.findById(id);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String principal = (String)auth.getPrincipal();
            User u = userdb.findById(userId);
            if(!principal.equals(u.getNickname())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if(!subscription.getUser().equals(userId))
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            else {
                subscriptiondb.deleteById(id);
                return ResponseEntity.noContent()
                        .header(HttpHeaders.LINK, all.toString())
                        .build();
            }
        }
    }
}
