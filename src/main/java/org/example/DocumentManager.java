package org.example;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class DocumentManager {
    private final Map<String, Document> db = new HashMap<>();

    public static void main(String[] args) {
        DocumentManager documentManager = new DocumentManager();

        // Test Data
        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("01")
                .name("Pavlo")
                .build();

        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .id(null)
                .title("My test task")
                .content("Create by Pavlo Levko")
                .author(author)
                .created(Instant.now())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document1);
        System.out.println("Saved Document: " + savedDocument);

        DocumentManager.SearchRequest searchRequest = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Intro"))
                .authorIds(List.of("author1"))
                .build();

        List<DocumentManager.Document> foundDocuments = documentManager.search(searchRequest);
        System.out.println("Found Documents: " + foundDocuments);

        Optional<DocumentManager.Document> documentById = documentManager.findById(savedDocument.getId());
        documentById.ifPresent(doc -> System.out.println("Document by ID: " + doc));
    }


    public Document save(Document document) {
        if (document.getId() == null) {
            document.setId(UUID.randomUUID().toString());
            document.setCreated(Instant.now());
        } else {
            Document existingDocument = db.get(document.getId());
            if (existingDocument != null) {
                document.setCreated(existingDocument.getCreated());
            }
        }
        db.put(document.getId(), document);
        return document;
    }

    public List<Document> search(SearchRequest request) {
        return db.values().stream()
                .filter(doc -> request.getTitlePrefixes() == null ||
                        request.getTitlePrefixes().stream().anyMatch(prefix -> doc.getTitle().startsWith(prefix)))
                .filter(doc -> request.getContainsContents() == null ||
                        request.getContainsContents().stream().anyMatch(content -> doc.getContent().contains(content)))
                .filter(doc -> request.getAuthorIds() == null ||
                        request.getAuthorIds().contains(doc.getAuthor().getId()))
                .filter(doc -> request.getCreatedFrom() == null ||
                        !doc.getCreated().isBefore(request.getCreatedFrom()))
                .filter(doc -> request.getCreatedTo() == null ||
                        !doc.getCreated().isAfter(request.getCreatedTo()))
                .collect(Collectors.toList());
    }


    public Optional<Document> findById(String id) {
        return Optional.ofNullable(db.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}

