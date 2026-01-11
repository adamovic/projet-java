package com.university.library.services;

import com.university.library.dao.LivreDAO;
import com.university.library.models.Livre;

import java.sql.SQLException;
import java.util.List;

public class LivreService {

    private final LivreDAO livreDAO = new LivreDAO();

    public List<Livre> getAllLivres() {
        return livreDAO.findAll();
    }

    public void ajouterLivre(Livre livre) throws SQLException {
        if (livreDAO.exists(livre.getIsbn())) {
            throw new IllegalStateException("Un livre avec cet ISBN existe déjà");
        }
        livreDAO.save(livre);
    }

    public void modifierLivre(Livre livre) {
        if (!livreDAO.exists(livre.getIsbn())) {
            throw new IllegalStateException("Livre introuvable pour la mise à jour");
        }
        livreDAO.update(livre);
    }

    public void supprimerLivre(String isbn) {
        if (!livreDAO.exists(isbn)) {
            throw new IllegalStateException("Livre introuvable pour la suppression");
        }
        livreDAO.delete(isbn);
    }

    public Livre trouverParIsbn(String isbn) {
        return livreDAO.findByIsbn(isbn);
    }
}
