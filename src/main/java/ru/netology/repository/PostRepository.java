package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

// Stub
public class PostRepository {

    private final ConcurrentMap<Long, Post> postMap;
    private final AtomicLong postMapCounter = new AtomicLong();

    public PostRepository() {
        this.postMap = new ConcurrentHashMap<>();
        this.postMapCounter.set(0);
    }

    public List<Post> all() {
        if (postMap.isEmpty()) throw new NotFoundException("Список постов пуст");
        return new ArrayList<>(postMap.values());
    }

    public Optional<Post> getById(long id) {
        return Optional.ofNullable(postMap.get(id));
    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            post.setId(postMapCounter.incrementAndGet());
            postMap.put(post.getId(), post);
            return post;
        }
        if (postMap.containsKey(post.getId())) {
            postMap.put(post.getId(), post);
            return post;
        }
        throw new NotFoundException("Не найден пост с указанным id");
    }

    public void removeById(long id) {

        if (postMap.containsKey(id)) {
            postMap.remove(id);
            return;
        }
        throw new NotFoundException("Пост с указанным id не найден");
    }

}
