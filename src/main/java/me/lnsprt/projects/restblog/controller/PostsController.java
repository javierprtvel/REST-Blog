package me.lnsprt.projects.restblog.controller;

import io.swagger.annotations.*;
import me.lnsprt.projects.restblog.model.Comment;
import me.lnsprt.projects.restblog.model.Post;
import me.lnsprt.projects.restblog.model.User;
import me.lnsprt.projects.restblog.repository.CommentRepository;
import me.lnsprt.projects.restblog.repository.UserRepository;
import me.lnsprt.projects.restblog.repository.query.CommentQueryInterpreter;
import me.lnsprt.projects.restblog.repository.query.PostQueryInterpreter;
import me.lnsprt.projects.restblog.repository.PostRepository;
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
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
@Api(
        tags = "REST-Blog Posts API",
        description = "REST API for CMS post and comment management",
        produces = "application/json, application/xml",
        consumes = "application/json, application/xml"
)
public class PostsController {
    private PostRepository postdb;
    private CommentRepository commentdb;
    private UserRepository userdb;
    private PostQueryInterpreter postqi;
    private CommentQueryInterpreter commentqi;
    private RelProvider relProvider;

    @Autowired
    public PostsController(PostRepository postdb, CommentRepository commentdb, UserRepository userdb,
                           PostQueryInterpreter postqi, CommentQueryInterpreter commentqi,
                           RelProvider relProvider) {
        this.postdb = postdb;
        this.commentdb = commentdb;
        this.userdb = userdb;
        this.postqi = postqi;
        this.commentqi = commentqi;
        this.relProvider = relProvider;
    }


    @ApiOperation("Get posts list")
    @ApiResponses({
            @ApiResponse(code=200, message = "The list of posts", response = Post.class, responseContainer = "List", responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            }),
    })
    @PreAuthorize("permitAll()")
    @GetMapping(
            produces = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    @ResponseStatus(HttpStatus.OK)
    public  ResponseEntity<Page<Post>> getPosts(@RequestParam(value = "author", required = false) String author,
                                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                @RequestParam(value= "sort", defaultValue = "date") String sortProperty,
                                                @RequestParam(value= "sortDir", defaultValue = "desc") String sortDir){
        Page<Post> posts;
        if(author != null) { // return 404 if author doesn't exist?
            posts = postdb.findByAuthor(PageRequest.of(page, size, new Sort(Sort.Direction.fromString(sortDir), sortProperty)), author);
        }
        else {
            posts = postdb.findAll(PageRequest.of(page, size, new Sort(Sort.Direction.fromString(sortDir), sortProperty)));
        }

        ResponseEntity.BodyBuilder response = ResponseEntity.ok();

        Link self = ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(PostsController.class).getPosts(author, page, size, sortProperty, sortDir))
                .withSelfRel();
        response.header(HttpHeaders.LINK, self.toString());

        if(!posts.isFirst()) {
            Link first = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(PostsController.class).getPosts(author, 0, size, sortProperty, sortDir))
                    .withRel(Link.REL_FIRST).expand();
            response.header(HttpHeaders.LINK, first.toString());

            Link prev = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(PostsController.class).getPosts(author, page - 1, size, sortProperty, sortDir))
                    .withRel(Link.REL_PREVIOUS).expand();
            response.header(HttpHeaders.LINK, prev.toString());
        }

        if(!posts.isLast()) {
            Link last = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(PostsController.class).getPosts(author, posts.getTotalPages() - 1, size, sortProperty, sortDir))
                    .withRel(Link.REL_LAST).expand();
            response.header(HttpHeaders.LINK, last.toString());

            Link next = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(PostsController.class).getPosts(author, page + 1, size, sortProperty, sortDir))
                    .withRel(Link.REL_NEXT).expand();
            response.header(HttpHeaders.LINK, next.toString());
        }

        return response.body(posts);
    }

    @ApiOperation("Search posts")
    @ApiResponses({
            @ApiResponse(code=200, message = "The list of filtered posts", response = Post.class, responseContainer = "List", responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            }),
            @ApiResponse(code=400, message = "Search query is syntactically or semantically incorrect")
    })
    @PreAuthorize("permitAll()")
    @GetMapping(
            path = "/search",
            produces = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    @ResponseStatus(HttpStatus.OK)
    public  ResponseEntity<Page<Post>> searchPosts(@RequestParam(value = "q") String searchQuery,
                                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                @RequestParam(value= "sort", defaultValue = "date") String sortProperty,
                                                @RequestParam(value= "sortDir", defaultValue = "desc") String sortDir){
        try {
            Page<Post> posts = postqi.executeQuery(searchQuery, PageRequest.of(page, size, new Sort(Sort.Direction.fromString(sortDir), sortProperty)));

            ResponseEntity.BodyBuilder response = ResponseEntity.ok();

            Link self = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(PostsController.class).searchPosts(searchQuery, page, size, sortProperty, sortDir))
                    .withSelfRel();
            response.header(HttpHeaders.LINK, self.toString());

            if(!posts.isFirst()) {
                Link first = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(PostsController.class).searchPosts(searchQuery, 0, size, sortProperty, sortDir))
                        .withRel(Link.REL_FIRST).expand();
                response.header(HttpHeaders.LINK, first.toString());

                Link prev = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(PostsController.class).searchPosts(searchQuery, page - 1, size, sortProperty, sortDir))
                        .withRel(Link.REL_PREVIOUS).expand();
                response.header(HttpHeaders.LINK, prev.toString());
            }

            if(!posts.isLast()) {
                Link last = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(PostsController.class).searchPosts(searchQuery, posts.getTotalPages() - 1, size, sortProperty, sortDir))
                        .withRel(Link.REL_LAST).expand();
                response.header(HttpHeaders.LINK, last.toString());

                Link next = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(PostsController.class).searchPosts(searchQuery, page + 1, size, sortProperty, sortDir))
                        .withRel(Link.REL_NEXT).expand();
                response.header(HttpHeaders.LINK, next.toString());
            }

            return response.body(posts);
        }
        catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @ApiOperation("Get a post")
    @ApiResponses({
            @ApiResponse(code=200, message = "The post", response = Post.class, responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            }),
            @ApiResponse(code=404, message = "Post not found")
    })
    @PreAuthorize("permitAll()")
    @GetMapping(
            path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Post> getPost(@PathVariable("id") String id) {

        if(postdb.existsById(id)) {
            Link self = ControllerLinkBuilder.linkTo(PostsController.class).slash(id).withSelfRel();
            Link collection = ControllerLinkBuilder.linkTo(PostsController.class).withRel(relProvider.getCollectionResourceRelFor(Post.class));
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.LINK, self.toString())
                    .header(HttpHeaders.LINK, collection.toString())
                    .body(postdb.findById(id));
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @ApiOperation("Get a post by its title")
    @ApiResponses({
            @ApiResponse(code=200, message = "The post", response = Post.class, responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            }),
            @ApiResponse(code=404, message = "Post not found")
    })
    @PreAuthorize("permitAll()")
    @GetMapping(
            path = "/title/{title}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Post> getPostByTitle(@PathVariable("title") String title) {

        if(postdb.existsByTitle(title)) {
            Link self = ControllerLinkBuilder.linkTo(PostsController.class).slash("/title").slash(title).withSelfRel();
            Link collection = ControllerLinkBuilder.linkTo(PostsController.class).withRel(relProvider.getCollectionResourceRelFor(Post.class));
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.LINK, self.toString())
                    .header(HttpHeaders.LINK, collection.toString())
                    .body(postdb.findByTitle(title));
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @ApiOperation("Create a post")
    @ApiResponses({
            @ApiResponse(code=401, message = "Only logged users can access this resource"),
            @ApiResponse(code=409, message = "Post already exists or the author is not the logged user"),
            @ApiResponse(code=403, message = "Only users with role EDITOR can create posts"),
            @ApiResponse(code=201, message = "Post created correctly", responseHeaders = {
                    @ResponseHeader(name = "Location", description = "The location where the created post can be found", response = URI.class),
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class, responseContainer = "List")
            })
    })
    @PreAuthorize("hasRole('EDITOR')")
    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Post> createPost(@RequestBody Post newPost) {

        if(postdb.existsByTitle(newPost.getTitle()) || !userdb.existsById(newPost.getAuthor()))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String principal = (String)auth.getPrincipal();
            User u = userdb.findById(newPost.getAuthor());
            if(!principal.equals(u.getNickname()))
                return ResponseEntity.status(HttpStatus.CONFLICT).build();

            Post post = new Post();
            post.setTitle(newPost.getTitle());
            post.setAuthor(newPost.getAuthor());
            post.setBody(newPost.getBody());
            post.setSummary(newPost.getSummary());
            post.setTags(newPost.getTags());
            post.setDate(new Date());

            post = postdb.save(post);

            Link self = ControllerLinkBuilder.linkTo(PostsController.class).slash(post.getId()).withSelfRel();
            Link collection = ControllerLinkBuilder.linkTo(PostsController.class).withRel(relProvider.getCollectionResourceRelFor(Post.class));
            return ResponseEntity
                    .created(URI.create(self.getHref()))
                    .header(HttpHeaders.LINK, self.toString())
                    .header(HttpHeaders.LINK, collection.toString())
                    .build();
        }
    }

    @ApiOperation("Update a existing post state")
    @ApiResponses({
            @ApiResponse(code=401, message = "Only logged users can access this resource"),
            @ApiResponse(code=409, message = "New post data  is not correct"),
            @ApiResponse(code=403, message = "Only users with role EDITOR can update post state"),
            @ApiResponse(code=404, message = "Post not found"),
            @ApiResponse(code=200, message = "Post modified correctly")
    })
    @PreAuthorize("hasRole('EDITOR')")
    @PutMapping(
            path = "/{id}",
            consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity modifyPost(@PathVariable("id") String id, @RequestBody Post updatedPost) {
        /* single method with multiple options based on credentials and roles, or several method for various
         * update actions with post resources?
         */
        if(!postdb.existsById(id))
            return ResponseEntity.notFound().build();
        else {
            // an editor can only modify its own posts
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String principal = (String)auth.getPrincipal();
            Post currentPost = postdb.findById(id);
            User u = userdb.findById(currentPost.getAuthor());
            if(!principal.equals(u.getNickname()))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

            if(currentPost.getAuthor().equals(updatedPost.getAuthor()) && // author reference cannot change
                    (currentPost.getTitle().equals(updatedPost.getTitle()) || !postdb.existsByTitle(updatedPost.getTitle()))) { // title must be unique
                updatedPost.setId(currentPost.getId());
                updatedPost.setDate(currentPost.getDate());
                postdb.save(updatedPost);

                return ResponseEntity.ok().build();
            }
            else {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }
    }

    @ApiOperation("Delete a post")
    @ApiResponses({
            @ApiResponse(code=204, message = "Post deleted correctly", responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            }),
            @ApiResponse(code=401, message = "Only logged users can access this resource"),
            @ApiResponse(code=403, message = "Only users with role ADMIN or EDITOR (upon a post of its own) can access this resource"),
            @ApiResponse(code=404, message = "Post not found")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity deletePost(@PathVariable("id") String id) {
        Link all = ControllerLinkBuilder.linkTo(PostsController.class).withRel(relProvider.getCollectionResourceRelFor(Post.class));

        if(!postdb.existsById(id))
            return ResponseEntity
                    .notFound()
                    .header(HttpHeaders.LINK, all.toString())
                    .build();
        else {
            // users without admin access level can only delete their own posts
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
            String principal = (String)auth.getPrincipal();
            Post post = postdb.findById(id);
            User u = userdb.findById(post.getAuthor());
            if(!authorities.contains("ADMIN") && !principal.equals(u.getNickname()))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

            postdb.deleteById(id);
            commentdb.deleteByPost(id);

            return ResponseEntity
                    .noContent()
                    .header(HttpHeaders.LINK, all.toString())
                    .build();
        }
    }

    @ApiOperation("Get user comments")
    @ApiResponses({
            @ApiResponse(code=200, message = "The list of comments", response = Comment.class, responseContainer = "List", responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            }),
            @ApiResponse(code=401, message = "Only logged users can access this resource"),
            @ApiResponse(code=403, message = "Only users with role MODERATOR or READER (upon own comments) can access this resource"),
            @ApiResponse(code=404, message = "User not found")
    })
    @PreAuthorize("hasRole('READER') or hasRole('MODERATOR')")
    @GetMapping(
            path = "/comments",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<Comment>> getUserComments(@RequestParam("user") String user,
                                                         @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                         @RequestParam(value= "sort", defaultValue = "date") String sortProperty,
                                                         @RequestParam(value= "sortDir", defaultValue = "desc") String sortDir) {
        // return 404 if user doesn't exist?
        if(!userdb.existsById(user))
            return ResponseEntity.notFound().build();
        else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
            String principal = (String)auth.getPrincipal();
            User u = userdb.findById(user);
            if(!authorities.contains("MODERATOR") && !principal.equals(u.getNickname()))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

            Page<Comment> comments = commentdb.findByUser(user, PageRequest.of(page, size, new Sort(Sort.Direction.fromString(sortDir), sortProperty)));

            ResponseEntity.BodyBuilder response = ResponseEntity.ok();

            Link self = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(PostsController.class).getUserComments(user, page, size, sortProperty, sortDir))
                    .withSelfRel();
            response.header(HttpHeaders.LINK, self.toString());

            if(!comments.isFirst()) {
                Link first = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(PostsController.class).getUserComments(user, 0, size, sortProperty, sortDir))
                        .withRel(Link.REL_FIRST).expand();
                response.header(HttpHeaders.LINK, first.toString());

                Link prev = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(PostsController.class).getUserComments(user, page - 1, size, sortProperty, sortDir))
                        .withRel(Link.REL_PREVIOUS).expand();
                response.header(HttpHeaders.LINK, prev.toString());
            }

            if(!comments.isLast()) {
                Link last = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(PostsController.class).getUserComments(user, comments.getTotalPages() - 1, size, sortProperty, sortDir))
                        .withRel(Link.REL_LAST).expand();
                response.header(HttpHeaders.LINK, last.toString());

                Link next = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(PostsController.class).getUserComments(user, page + 1, size, sortProperty, sortDir))
                        .withRel(Link.REL_NEXT).expand();
                response.header(HttpHeaders.LINK, next.toString());
            }

            return response.body(comments);
        }
    }

    @ApiOperation("Get post comments")
    @ApiResponses({
            @ApiResponse(code=200, message = "The list of comments", response = Comment.class, responseContainer = "List", responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            }),
            @ApiResponse(code=404, message = "Post not found")
    })
    @PreAuthorize("permitAll()")
    @GetMapping(
            path = "/{id}/comments",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<Comment>> getPostComments(@PathVariable("id") String id,
                                                         @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                         @RequestParam(value= "sort", defaultValue = "date") String sortProperty,
                                                         @RequestParam(value= "sortDir", defaultValue = "desc") String sortDir) {
        if(!postdb.existsById(id))
            return ResponseEntity.notFound().build();
        else {
            Page<Comment> comments = commentdb.findByPost(id, PageRequest.of(page, size, new Sort(Sort.Direction.fromString(sortDir), sortProperty)));

            ResponseEntity.BodyBuilder response = ResponseEntity.ok();

            Link self = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(PostsController.class).getPostComments(id, page, size, sortProperty, sortDir))
                    .withSelfRel();
            response.header(HttpHeaders.LINK, self.toString());

            if(!comments.isFirst()) {
                Link first = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(PostsController.class).getPostComments(id, 0, size, sortProperty, sortDir))
                        .withRel(Link.REL_FIRST).expand();
                response.header(HttpHeaders.LINK, first.toString());

                Link prev = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(PostsController.class).getPostComments(id, page - 1, size, sortProperty, sortDir))
                        .withRel(Link.REL_PREVIOUS).expand();
                response.header(HttpHeaders.LINK, prev.toString());
            }

            if(!comments.isLast()) {
                Link last = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(PostsController.class).getPostComments(id, comments.getTotalPages() - 1, size, sortProperty, sortDir))
                        .withRel(Link.REL_LAST).expand();
                response.header(HttpHeaders.LINK, last.toString());

                Link next = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(PostsController.class).getPostComments(id, page + 1, size, sortProperty, sortDir))
                        .withRel(Link.REL_NEXT).expand();
                response.header(HttpHeaders.LINK, next.toString());
            }

            return response.body(comments);
        }
    }

    @ApiOperation("Search comments")
    @ApiResponses({
            @ApiResponse(code=200, message = "The list of filtered comments", response = Comment.class, responseContainer = "List", responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            }),
            @ApiResponse(code=401, message = "Only logged users can access this resource"),
            @ApiResponse(code=400, message = "Search query is syntactically or semantically incorrect"),
            @ApiResponse(code=403, message = "Only users with role MODERATOR or READER can access this resource")
    })
    @PreAuthorize("hasRole('READER') or hasRole('MODERATOR')")
    @GetMapping(
            path = "/comments/search",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<Comment>> searchComments(@RequestParam(value = "q") String searchQuery,
                                                     @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                     @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                     @RequestParam(value= "sort", defaultValue = "date") String sortProperty,
                                                     @RequestParam(value= "sortDir", defaultValue = "desc") String sortDir) {
        try {
            Page<Comment> comments = commentqi.executeQuery(searchQuery, PageRequest.of(page, size, new Sort(Sort.Direction.fromString(sortDir), sortProperty)));

            ResponseEntity.BodyBuilder response = ResponseEntity.ok();

            Link self = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(PostsController.class).searchComments(searchQuery, page, size, sortProperty, sortDir))
                    .withSelfRel();
            response.header(HttpHeaders.LINK, self.toString());

            if(!comments.isFirst()) {
                Link first = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(PostsController.class).searchComments(searchQuery, 0, size, sortProperty, sortDir))
                        .withRel(Link.REL_FIRST).expand();
                response.header(HttpHeaders.LINK, first.toString());

                Link prev = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(PostsController.class).searchComments(searchQuery, page - 1, size, sortProperty, sortDir))
                        .withRel(Link.REL_PREVIOUS).expand();
                response.header(HttpHeaders.LINK, prev.toString());
            }

            if(!comments.isLast()) {
                Link last = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(PostsController.class).searchComments(searchQuery, comments.getTotalPages() - 1, size, sortProperty, sortDir))
                        .withRel(Link.REL_LAST).expand();
                response.header(HttpHeaders.LINK, last.toString());

                Link next = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(PostsController.class).searchComments(searchQuery, page + 1, size, sortProperty, sortDir))
                        .withRel(Link.REL_NEXT).expand();
                response.header(HttpHeaders.LINK, next.toString());
            }

            return response.body(comments);
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @ApiOperation("Get a post comment")
    @ApiResponses({
            @ApiResponse(code=200, message = "The comment", response = Comment.class, responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            }),
            @ApiResponse(code=404, message = "Post or comment not found"),
            @ApiResponse(code=409, message = "Post and comment are not related")
    })
    @PreAuthorize("permitAll()")
    @GetMapping(
            path = "/{postId}/comments/{id}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Comment> getComment(@PathVariable("postId")String postId, @PathVariable("id") String id) {

        if(postdb.existsById(postId) && commentdb.existsById(id)) {
            Comment c = commentdb.findById(id);

            if(postId.equals(c.getPost())) {
                Link self = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(PostsController.class).getComment(postId, id))
                        .withSelfRel();
                Link collection = ControllerLinkBuilder.linkTo(PostsController.class)
                        .slash(postId)
                        .slash("comments")
                        .withRel(relProvider.getCollectionResourceRelFor(Comment.class));
                return ResponseEntity
                        .ok()
                        .header(HttpHeaders.LINK, self.toString())
                        .header(HttpHeaders.LINK, collection.toString())
                        .body(c);
            }
            else {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }
        else
            return ResponseEntity.notFound().build();
    }

    @ApiOperation("Create a comment in a post")
    @ApiResponses({
            @ApiResponse(code=401, message = "Only logged users can access this resource"),
            @ApiResponse(code=409, message = "Comment data is not correct"),
            @ApiResponse(code=403, message = "Only users with role READER can create comments"),
            @ApiResponse(code=404, message = "Post not found"),
            @ApiResponse(code=201, message = "Comment created correctly", responseHeaders = {
                    @ResponseHeader(name = "Location", description = "The location where the created comment can be found", response = URI.class),
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class, responseContainer = "List")
            })
    })
    @PreAuthorize("hasRole('READER')")
    @PostMapping(
            path = "/{id}/comments",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Comment> createComment(@PathVariable("id") String id, @RequestBody Comment newComment) {
        if(!postdb.existsById(id))
            return ResponseEntity.notFound().build();
        else {
            Post post = postdb.findById(id);
            String postId = newComment.getPost();
            String userId = newComment.getUser();
            if(!userdb.existsById(userId) || !post.getId().equals(postId))
                return ResponseEntity.status(HttpStatus.CONFLICT).build();

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String principal = (String)auth.getPrincipal();
            if(!principal.equals(userdb.findById(userId).getNickname()))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

            newComment.setDate(new Date());
            newComment = commentdb.save(newComment);

            Link self = ControllerLinkBuilder.linkTo(PostsController.class)
                    .slash(id)
                    .slash("comments")
                    .slash(newComment.getId()).withSelfRel();
            Link collection = ControllerLinkBuilder.linkTo(PostsController.class)
                    .slash(id)
                    .slash("comments")
                    .withRel(relProvider.getCollectionResourceRelFor(Comment.class));
            return ResponseEntity
                    .created(URI.create(self.getHref()))
                    .header(HttpHeaders.LINK, self.toString())
                    .header(HttpHeaders.LINK, collection.toString())
                    .build();
        }
    }

    @ApiOperation("Delete a comment")
    @ApiResponses({
            @ApiResponse(code=204, message = "Comment deleted correctly", responseHeaders = {
                    @ResponseHeader(name = "Link", description = "Related links", response = Link.class)
            }),
            @ApiResponse(code=401, message = "Only logged users can access this resource"),
            @ApiResponse(code=403, message = "Only users with role MODERATOR or READER (upon a comment of its own) can access this resource"),
            @ApiResponse(code=404, message = "Post or comment not found"),
            @ApiResponse(code=409, message = "Post and comment are not related")
    })
    @PreAuthorize("hasRole('MODERATOR') or hasRole('READER')")
    @DeleteMapping(path = "/{postId}/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity deleteComment(@PathVariable("postId") String postId, @PathVariable("id") String id) {
        Link all = ControllerLinkBuilder.linkTo(PostsController.class)
                .slash(postId)
                .slash("comments")
                .withRel(relProvider.getCollectionResourceRelFor(Comment.class));

        if(!postdb.existsById(postId) || !commentdb.existsById(id)) {
            return ResponseEntity
                    .notFound()
                    .header(HttpHeaders.LINK, all.toString())
                    .build();
        }
        else {
            Comment c = commentdb.findById(id);

            Post p = postdb.findById(postId);
            if(!c.getPost().equals(p.getId()))
                return ResponseEntity.status(HttpStatus.CONFLICT).build();

            // users without moderator access level can only delete their own comments
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
            String principal = (String)auth.getPrincipal();
            User u = userdb.findById(c.getUser());
            if(!authorities.contains("MODERATOR")  && !authorities.contains("EDITOR") && !authorities.contains("ADMIN")
                    && !principal.equals(u.getNickname()))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

            commentdb.deleteById(id);

            return ResponseEntity
                    .noContent()
                    .header(HttpHeaders.LINK, all.toString())
                    .build();
        }
    }
}
