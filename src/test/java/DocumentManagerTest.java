import org.example.DocumentManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DocumentManagerTest {

    private DocumentManager documentManager;

    @BeforeEach
    void setUp() {
        documentManager = new DocumentManager();
    }

    @Test
    void testSaveAndFindById() {
        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("author1")
                .name("John Doe")
                .build();

        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Test Title")
                .content("Test Content")
                .author(author)
                .created(Instant.now())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);
        assertNotNull(savedDocument.getId(), "ID must be generated!");


        Optional<DocumentManager.Document> foundDocument = documentManager.findById(savedDocument.getId());
        assertTrue(foundDocument.isPresent(), "Document must be found");
        assertEquals("Test Title", foundDocument.get().getTitle(), "Document name are different");
    }

    @Test
    void testSearch() {
        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("author1")
                .name("John Doe")
                .build();

        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .title("Intro to Java")
                .content("Learn Java")
                .author(author)
                .created(Instant.now())
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .title("Advanced Python")
                .content("Learn Python")
                .author(author)
                .created(Instant.now())
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        DocumentManager.SearchRequest searchRequest = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Intro"))
                .build();

        List<DocumentManager.Document> searchResults = documentManager.search(searchRequest);
        assertEquals(1, searchResults.size(), "Must be only one document");
        assertEquals("Intro to Java", searchResults.get(0).getTitle(), "Document name are different");
    }
}