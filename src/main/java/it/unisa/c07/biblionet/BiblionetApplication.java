package it.unisa.c07.biblionet;

import it.unisa.c07.biblionet.prenotazioneLibri.service.bookApiAdapter.BookApiAdapter;
import it.unisa.c07.biblionet.prenotazioneLibri.service.bookApiAdapter.GoogleBookApiAdapterImpl;
import it.unisa.c07.biblionet.model.dao.PossessoDAO;
import it.unisa.c07.biblionet.model.dao.TicketPrestitoDAO;
import it.unisa.c07.biblionet.model.dao.LibroDAO;
import it.unisa.c07.biblionet.model.dao.GenereDAO;
import it.unisa.c07.biblionet.model.dao.utente.BibliotecaDAO;
import it.unisa.c07.biblionet.model.entity.Possesso;
import it.unisa.c07.biblionet.model.entity.Libro;
import it.unisa.c07.biblionet.model.entity.TicketPrestito;
import it.unisa.c07.biblionet.model.entity.Genere;
import it.unisa.c07.biblionet.model.entity.utente.Biblioteca;
import it.unisa.c07.biblionet.model.entity.compositeKey.PossessoId;

import org.springframework.context.ApplicationContext;
import org.apache.commons.io.FileUtils;
import org.jboss.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;

/**
 * Questa è la main class del progetto, che fa partire l'applicazione e popola
 * il database.
 */
@SpringBootApplication
public class BiblionetApplication {

    private static BookApiAdapter bookApiAdapter = new GoogleBookApiAdapterImpl();

    private static LibroDAO libroDAO = null;

    public static Libro getLibroFromAPI(String isbn, Genere... generi) {
        Libro libro = bookApiAdapter.getLibroDaBookApi(isbn);
        if (libro == null) {
            libro = new Libro("Not Found", "Autore", isbn,
                    LocalDateTime.now(), "Descrizione", "Casa Editrice");
        }
        libro.setGeneri(Arrays.asList(generi));

        return libroDAO.save(libro);
    }



    public static void main(String[] args) throws NoSuchAlgorithmException {
        ConfigurableApplicationContext configurableApplicationContext =
                SpringApplication.run(BiblionetApplication.class, args);

        //init(configurableApplicationContext);
    }

    public static String getCopertinaClubFromUrl(String filePath) {
        try{
            byte[] fileContent = FileUtils.readFileToByteArray(new File(filePath));
            return Base64.getEncoder().encodeToString(fileContent);
        }
        catch (IOException ex){
            ex.printStackTrace();
            return "noimage";
        }

    }
}
