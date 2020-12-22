package it.unisa.c07.biblionet.autenticazione.service;

import it.unisa.c07.biblionet.model.dao.utente.BibliotecaDAO;
import it.unisa.c07.biblionet.model.dao.utente.EspertoDAO;
import it.unisa.c07.biblionet.model.dao.utente.LettoreDAO;
import it.unisa.c07.biblionet.model.entity.utente.UtenteRegistrato;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *Questa classe implementa i metodi di AutenticazioneService.
 */
@Service
@RequiredArgsConstructor
public class AutenticazioneServiceImpl implements AutenticazioneService {


    private final LettoreDAO lettoreDAO;
    private final BibliotecaDAO bibliotecaDAO;
    private final EspertoDAO espertoDAO;
    /**
     * @param email dell'utente.
     * @param password dell'utente.
     * @return un utente registrato.
     */
    @Override
    public UtenteRegistrato login(final String email, final String password) {
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-256");
            byte[] arr = md.digest(password.getBytes());
                UtenteRegistrato u;
                if ((u = lettoreDAO.findByEmailAndPassword(email,arr))!=null){
                    return u;
                }
                else if ((u = bibliotecaDAO.findByEmailAndPassword(email,arr))!=null){
                    return u;
                }
                else {u = espertoDAO.findByEmailAndPassword(email,arr);
                    return u;
                }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean logout(UtenteRegistrato utenteRegistrato) {
        return false;
    }
}
