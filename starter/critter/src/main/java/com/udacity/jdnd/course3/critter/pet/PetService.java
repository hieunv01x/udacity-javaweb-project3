package com.udacity.jdnd.course3.critter.pet;

import com.udacity.jdnd.course3.critter.user.Customer;
import com.udacity.jdnd.course3.critter.user.CustomerRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final CustomerRepository customerRepository;

    public PetService(PetRepository petRepository, CustomerRepository customerRepository) {
        this.petRepository = petRepository;
        this.customerRepository = customerRepository;
    }

    public PetDTO getPet(long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find pet with ID = " + petId));
        return convertToPetDTO(pet);
    }

    public List<PetDTO> getPets() {
        List<Pet> petList = (List<Pet>) petRepository.findAll();
        return petList.stream().map(this::convertToPetDTO).collect(Collectors.toList());
    }

    public List<PetDTO> findPetsByCustomer(long customerId) {
        List<Pet> petList = petRepository.findPetsByCustomer_CustomerId(customerId);
        return petList.stream().map(this::convertToPetDTO).collect(Collectors.toList());
    }

    public PetDTO savePet(PetDTO petDTO) {
        Pet pet = new Pet();
        try {
            convertToPet(pet, petDTO);
            petRepository.save(pet);
            return this.convertToPetDTO(pet);
        } catch (Exception e) {
            return null;
        }
    }

    private void convertToPet(Pet pet, PetDTO petDTO) {
        Customer customer = customerRepository.findById(petDTO.getOwnerId())
                .orElseThrow(() -> new EntityNotFoundException(("Customer not found")));
        pet.setType(petDTO.getType());
        pet.setName(petDTO.getName());
        pet.setCustomer(customer);
        pet.setBirthDate(petDTO.getBirthDate());
        pet.setNotes(petDTO.getNotes());
    }

    private PetDTO convertToPetDTO(Pet pet) {
        PetDTO petDTO = new PetDTO();
        petDTO.setId(pet.getPetId());
        petDTO.setType(pet.getType());
        petDTO.setName(pet.getName());
        petDTO.setOwnerId(pet.getCustomer().getCustomerId());
        petDTO.setBirthDate(pet.getBirthDate());
        petDTO.setNotes(pet.getNotes());
        return petDTO;
    }
}
