package com.api.automation.tests.smoke;

import com.api.automation.models.Post;
import com.api.automation.services.PostService;
import com.api.automation.tests.base.BaseTest;
import com.api.automation.utils.JsonPathUtils;
import com.api.automation.utils.TestDataUtils;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke tests for Post API endpoints
 */
@Tag("smoke")
@Tag("posts")
@Epic("Content Management")
@Feature("Post API")
public class PostSmokeTest extends BaseTest {

    private final PostService postService = new PostService();

    @Test
    @DisplayName("Get All Posts - Smoke Test")
    @Description("Verify that the get all posts endpoint returns a successful response")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Get All Posts")
    void testGetAllPosts() {
        logStep("Send GET request to retrieve all posts");
        
        Response response = postService.getAllPosts();
        
        logStep("Verify response status code is 200");
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("size()", greaterThan(0));
        
        logVerification("Successfully retrieved all posts with status code 200");
        
        // Additional validations
        String responseBody = response.getBody().asString();
        assertTrue(JsonPathUtils.pathExists(responseBody, "[0].id"), "First post should have an ID");
        assertTrue(JsonPathUtils.pathExists(responseBody, "[0].title"), "First post should have a title");
        assertTrue(JsonPathUtils.pathExists(responseBody, "[0].body"), "First post should have a body");
        assertTrue(JsonPathUtils.pathExists(responseBody, "[0].userId"), "First post should have a userId");
        
        logVerification("All required fields are present in the response");
    }

    @Test
    @DisplayName("Get Post by ID - Smoke Test")
    @Description("Verify that getting a post by ID returns the correct post")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Get Post by ID")
    void testGetPostById() {
        Long postId = 1L;
        
        logStep("Send GET request to retrieve post with ID: " + postId);
        
        Response response = postService.getPostById(postId);
        
        logStep("Verify response status code is 200 and post data is correct");
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("id", equalTo(postId.intValue()))
                .body("title", notNullValue())
                .body("body", notNullValue())
                .body("userId", notNullValue());
        
        logVerification("Successfully retrieved post by ID with correct data");
        
        // Parse response to Post object
        Post post = postService.getPostByIdAsObject(postId);
        assertNotNull(post, "Post object should not be null");
        assertEquals(postId, post.getId(), "Post ID should match requested ID");
        assertNotNull(post.getTitle(), "Post title should not be null");
        assertNotNull(post.getBody(), "Post body should not be null");
        assertNotNull(post.getUserId(), "Post userId should not be null");
        
        logVerification("Post object deserialization successful");
    }

    @Test
    @DisplayName("Get Posts by User ID - Smoke Test")
    @Description("Verify that getting posts by user ID returns posts for that user")
    @Severity(SeverityLevel.NORMAL)
    @Story("Get Posts by User")
    void testGetPostsByUserId() {
        Long userId = 1L;
        
        logStep("Send GET request to retrieve posts for user ID: " + userId);
        
        Response response = postService.getPostsByUserId(userId);
        
        logStep("Verify response status code is 200 and posts belong to the user");
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("size()", greaterThan(0))
                .body("userId", everyItem(equalTo(userId.intValue())));
        
        logVerification("Successfully retrieved posts for user ID: " + userId);
        
        // Parse response to Post objects
        List<Post> posts = postService.getPostsByUserIdAsList(userId);
        assertFalse(posts.isEmpty(), "Posts list should not be empty");
        posts.forEach(post -> {
            assertEquals(userId, post.getUserId(), "All posts should belong to the specified user");
            assertNotNull(post.getTitle(), "Post title should not be null");
            assertNotNull(post.getBody(), "Post body should not be null");
        });
        
        logVerification("All posts belong to the correct user");
    }

    @Test
    @DisplayName("Create Post - Smoke Test")
    @Description("Verify that a new post can be created successfully")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Create Post")
    void testCreatePost() {
        logStep("Prepare test data for new post");
        
        // Load test data from JSON file
        List<Post> testPosts = TestDataUtils.readJsonTestDataAsList("posts.json", Post.class);
        Post newPost = testPosts.get(0); // Use first test post
        
        logStep("Send POST request to create new post: " + newPost.getTitle());
        
        Response response = postService.createPost(newPost);
        
        logStep("Verify response status code is 201 and post is created");
        response.then()
                .statusCode(201)
                .contentType("application/json")
                .body("title", equalTo(newPost.getTitle()))
                .body("body", equalTo(newPost.getBody()))
                .body("userId", equalTo(newPost.getUserId().intValue()));
        
        logVerification("Post created successfully with status code 201");
        
        // Verify the created post object
        Post createdPost = postService.createPostAndReturn(newPost);
        assertNotNull(createdPost.getId(), "Created post should have an ID");
        assertEquals(newPost.getTitle(), createdPost.getTitle(), "Titles should match");
        assertEquals(newPost.getBody(), createdPost.getBody(), "Bodies should match");
        assertEquals(newPost.getUserId(), createdPost.getUserId(), "User IDs should match");
        
        logVerification("Created post data matches the input data");
    }

    @Test
    @DisplayName("Update Post - Smoke Test")
    @Description("Verify that an existing post can be updated successfully")
    @Severity(SeverityLevel.NORMAL)
    @Story("Update Post")
    void testUpdatePost() {
        Long postId = 1L;
        
        logStep("Prepare updated post data");
        
        Post updatedPost = new Post();
        updatedPost.setUserId(1L);
        updatedPost.setTitle("Updated Test Post");
        updatedPost.setBody("This is the updated body of the test post.");
        
        logStep("Send PUT request to update post with ID: " + postId);
        
        Response response = postService.updatePost(postId, updatedPost);
        
        logStep("Verify response status code is 200 and post is updated");
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("id", equalTo(postId.intValue()))
                .body("title", equalTo(updatedPost.getTitle()))
                .body("body", equalTo(updatedPost.getBody()))
                .body("userId", equalTo(updatedPost.getUserId().intValue()));
        
        logVerification("Post updated successfully with correct data");
    }

    @Test
    @DisplayName("Delete Post - Smoke Test")
    @Description("Verify that an existing post can be deleted successfully")
    @Severity(SeverityLevel.NORMAL)
    @Story("Delete Post")
    void testDeletePost() {
        Long postId = 1L;
        
        logStep("Send DELETE request to delete post with ID: " + postId);
        
        Response response = postService.deletePost(postId);
        
        logStep("Verify response status code is 200");
        response.then()
                .statusCode(200);
        
        logVerification("Post deleted successfully");
    }

    @Test
    @DisplayName("Get Post Comments - Smoke Test")
    @Description("Verify that comments for a post can be retrieved")
    @Severity(SeverityLevel.MINOR)
    @Story("Get Post Comments")
    void testGetPostComments() {
        Long postId = 1L;
        
        logStep("Send GET request to retrieve comments for post ID: " + postId);
        
        Response response = postService.getPostComments(postId);
        
        logStep("Verify response status code is 200 and comments are returned");
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("size()", greaterThan(0));
        
        logVerification("Successfully retrieved comments for post ID: " + postId);
        
        // Verify comment structure
        String responseBody = response.getBody().asString();
        assertTrue(JsonPathUtils.pathExists(responseBody, "[0].id"), "First comment should have an ID");
        assertTrue(JsonPathUtils.pathExists(responseBody, "[0].name"), "First comment should have a name");
        assertTrue(JsonPathUtils.pathExists(responseBody, "[0].email"), "First comment should have an email");
        assertTrue(JsonPathUtils.pathExists(responseBody, "[0].body"), "First comment should have a body");
        
        logVerification("Comment structure validation successful");
    }
}
