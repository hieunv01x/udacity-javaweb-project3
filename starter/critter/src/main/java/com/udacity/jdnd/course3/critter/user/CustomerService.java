package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.pet.PetRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PetRepository petRepository;

    public CustomerService(CustomerRepository customerRepository, PetRepository petRepository) {
        this.customerRepository = customerRepository;
        this.petRepository = petRepository;
    }

    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customerList = (List<Customer>) customerRepository.findAll();
        return customerList.stream().map(this::convertToCustomerDTO).collect(Collectors.toList());
    }

    public CustomerDTO getOwnerByPet(long petId) {
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new EntityNotFoundException("Can't find the petId: " + petId));
        Customer customer = pet.getCustomer();
        return this.convertToCustomerDTO(customer);
    }

    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        if(this.convertToCustomer(customer, customerDTO)) {
            customerRepository.save(customer);
            return this.convertToCustomerDTO(customer);
        }
        return null;
    }

    private boolean convertToCustomer(Customer customer, CustomerDTO customerDTO) {
        customer.setName(customerDTO.getName());
        customer.setPhoneNumber(customerDTO.getPhoneNumber());
        customer.setNotes(customerDTO.getNotes());
        if(customerDTO.getPetIds() == null) return true;
        List<Pet> petList = petRepository.findPetsByCustomer_CustomerId(customerDTO.getId());
        if(petList.isEmpty()) return false;
        customer.setPets(petList);
        return true;
    }
    
    private CustomerDTO convertToCustomerDTO(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(customer.getCustomerId());
        customerDTO.setName(customer.getName());
        customerDTO.setPhoneNumber(customer.getPhoneNumber());
        customerDTO.setNotes(customer.getNotes());
        if (customer.getPets() != null) {
            List<Pet> petList = customer.getPets();
            customerDTO.setPetIds(petList.stream().map(Pet::getPetId).collect(Collectors.toList()));
        }
        return customerDTO;
    }
}
