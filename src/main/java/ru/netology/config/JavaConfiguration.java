package ru.netology.config;

import org.springframework.context.annotation.Bean;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

public class JavaConfiguration {

    @Bean
    public PostController postController(PostService service) {
        return new PostController(service);
    }

    @Bean
    public PostService postService(PostRepository postRepository) {
        return new PostService(postRepository);
    }

    @Bean
    public PostRepository postRepository(){
        return new PostRepository();
    }

}
