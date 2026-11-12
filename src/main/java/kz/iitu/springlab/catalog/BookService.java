package kz.iitu.springlab.catalog;

import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<Book> findAll(String author) {
        if (author == null || author.isBlank()) {
            return repository.findAll();
        }
        return repository.findAll().stream()
                .filter(b -> b.author().toLowerCase().contains(author.toLowerCase()))
                .toList();
    }

    public Optional<Book> findById(long id) {
        return repository.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Book create(Book book) {
        return repository.save(book);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<Book> update(long id, Book newBook) {
        if (repository.findById(id).isEmpty()) {
            return Optional.empty();
        }
        Book updated = new Book(id, newBook.title(), newBook.author(), newBook.year());
        return Optional.of(repository.save(updated));
    }

    // Individual task: PATCH
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<Book> patch(long id, Book patchBook) {
        return repository.findById(id).map(existing -> {
            String title = patchBook.title() != null ? patchBook.title() : existing.title();
            String author = patchBook.author() != null ? patchBook.author() : existing.author();
            Integer year = patchBook.year() != null ? patchBook.year() : existing.year();
            Book updated = new Book(id, title, author, year);
            return repository.save(updated);
        });
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean delete(long id) {
        return repository.deleteById(id);
    }
}
