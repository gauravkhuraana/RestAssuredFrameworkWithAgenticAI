package com.api.automation.services;

import com.api.automation.client.BaseApiClient;
import com.api.automation.models.Post;
import com.api.automation.utils.JsonUtils;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Post service for post-related API operations
 */
public class PostService extends BaseApiClient {
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    private static final String POSTS_ENDPOINT = "/posts";

    /**
     * Get all posts
     */
    public Response getAllPosts() {
        logger.info("Getting all posts");
        return get(POSTS_ENDPOINT);
    }

    /**
     * Get post by ID
     */
    public Response getPostById(Long postId) {
        logger.info("Getting post by ID: {}", postId);
        return withPathParam("id", postId)
                .get(POSTS_ENDPOINT + "/{id}");
    }

    /**
     * Get posts by user ID
     */
    public Response getPostsByUserId(Long userId) {
        logger.info("Getting posts by user ID: {}", userId);
        return withQueryParam("userId", userId)
                .get(POSTS_ENDPOINT);
    }

    /**
     * Create new post
     */
    public Response createPost(Post post) {
        logger.info("Creating new post: {}", post.getTitle());
        return withBody(post)
                .post(POSTS_ENDPOINT);
    }

    /**
     * Update post
     */
    public Response updatePost(Long postId, Post post) {
        logger.info("Updating post ID: {}", postId);
        return withPathParam("id", postId)
                .withBody(post)
                .put(POSTS_ENDPOINT + "/{id}");
    }

    /**
     * Partially update post
     */
    public Response patchPost(Long postId, Post post) {
        logger.info("Partially updating post ID: {}", postId);
        return withPathParam("id", postId)
                .withBody(post)
                .patch(POSTS_ENDPOINT + "/{id}");
    }

    /**
     * Delete post
     */
    public Response deletePost(Long postId) {
        logger.info("Deleting post ID: {}", postId);
        return withPathParam("id", postId)
                .delete(POSTS_ENDPOINT + "/{id}");
    }

    /**
     * Get all posts and parse to Post objects
     */
    public List<Post> getAllPostsAsList() {
        Response response = getAllPosts();
        response.then().statusCode(200);
        return JsonUtils.jsonToList(response.getBody().asString(), Post.class);
    }

    /**
     * Get post by ID and parse to Post object
     */
    public Post getPostByIdAsObject(Long postId) {
        Response response = getPostById(postId);
        response.then().statusCode(200);
        return JsonUtils.jsonToObject(response.getBody().asString(), Post.class);
    }

    /**
     * Create post and return created Post object
     */
    public Post createPostAndReturn(Post post) {
        Response response = createPost(post);
        response.then().statusCode(201);
        return JsonUtils.jsonToObject(response.getBody().asString(), Post.class);
    }

    /**
     * Get posts by user ID and parse to Post objects
     */
    public List<Post> getPostsByUserIdAsList(Long userId) {
        Response response = getPostsByUserId(userId);
        response.then().statusCode(200);
        return JsonUtils.jsonToList(response.getBody().asString(), Post.class);
    }

    /**
     * Search posts by title
     */
    public Response searchPostsByTitle(String title) {
        logger.info("Searching posts by title: {}", title);
        return withQueryParam("title", title)
                .get(POSTS_ENDPOINT);
    }

    /**
     * Get comments for a specific post
     */
    public Response getPostComments(Long postId) {
        logger.info("Getting comments for post ID: {}", postId);
        return withPathParam("id", postId)
                .get(POSTS_ENDPOINT + "/{id}/comments");
    }
}
