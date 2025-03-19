package com.duartecelo.crud_clientes.services;

import com.duartecelo.crud_clientes.entities.Client;
import com.duartecelo.crud_clientes.repositories.ClientRepository;
import com.duartecelo.crud_clientes.services.exceptions.DataBaseException;
import com.duartecelo.crud_clientes.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {

    @Autowired
    ClientRepository clientRepository;

    @Transactional
    public Client findById(Long id) {
        return clientRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente inexistente ou já deletado"));
    }

    @Transactional
    public Page<Client> findAll(Pageable pageable) {
        return clientRepository.findAll(pageable);
    }

    @Transactional
    public Client insert(Client client) {
        return clientRepository.save(client);
    }

    @Transactional
    public Client update(Long id, Client updatedClient) {
        try {
            Client originClient = clientRepository.getReferenceById(id);

            originClient.setName(updatedClient.getName());
            originClient.setCpf(updatedClient.getCpf());
            originClient.setIncome(updatedClient.getIncome());
            originClient.setBirthDate(updatedClient.getBirthDate());
            originClient.setChildren(updatedClient.getChildren());

            originClient = clientRepository.save(originClient);

            return originClient;
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Cliente inexistente ou já deletado");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!clientRepository.existsById((id))) throw new ResourceNotFoundException("Cliente inexistente ou já deletado");
        try {
            clientRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Falha de integridade referencial");
        }
    }
}
