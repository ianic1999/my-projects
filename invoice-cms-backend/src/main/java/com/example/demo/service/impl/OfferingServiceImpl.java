package com.example.demo.service.impl;

import com.example.demo.dto.OfferingDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.dto.request.OfferingRequest;
import com.example.demo.model.*;
import com.example.demo.model.enums.OfferingType;
import com.example.demo.model.enums.UserRole;
import com.example.demo.model.exception.InvoiceException;
import com.example.demo.model.exception.OfferingException;
import com.example.demo.model.exception.UserException;
import com.example.demo.repository.InvoiceRepository;
import com.example.demo.repository.OfferingRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OfferingServiceImpl implements OfferingService {
    private final OfferingRepository offeringRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;

    @Override
    public Page<OfferingDTO> get(int page, int perPage, UserDTO currentUser, String search) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        return currentUser.getRole().equals(UserRole.USER.getKey())
            ? offeringRepository.findByCreatedBy_EmailAndNameContainsIgnoreCase(pageable, currentUser.getEmail(), search).map(Offering::toDTO)
                : offeringRepository.findByNameContainsIgnoreCase(pageable, search).map(Offering::toDTO);
    }

    @Override
    public OfferingDTO getById(long id, UserDTO currentUser) throws OfferingException, UserException {
        Offering offeringFromDB = offeringRepository.findById(id).orElseThrow(() -> new OfferingException("Offering with id " + id + " doesn't exist"));
        if (!(offeringFromDB.getCreatedBy().getEmail().equals(currentUser.getEmail()) || currentUser.getRole().equals(UserRole.ADMIN.getKey())))
            throw new UserException("You try to get an offering that is not yours");
        return offeringFromDB.toDTO();
    }

    @Override
    public OfferingDTO add(OfferingRequest offering, UserDTO currentUser) throws OfferingException, InvoiceException, UserException {
        User user = userRepository.findByEmail(currentUser.getEmail()).orElseThrow(() -> new UserException("User with email " + currentUser.getEmail() + " doesn't exist"));
        if (!(offering.getType().equals("product") || offering.getType().equals("service")))
            throw new OfferingException("Offering type should be product or service");
        Offering offeringToAdd = Offering.builder()
                .name(offering.getName())
                .description(offering.getDescription())
                .price(offering.getPrice())
                .type(offering.getType().equals("product") ? OfferingType.PRODUCT : OfferingType.SERVICE)
                .createdBy(user)
                .build();
        return offeringRepository.save(offeringToAdd).toDTO();
    }

    @Override
    @Transactional
    public OfferingDTO update(OfferingRequest offering, UserDTO currentUser) throws OfferingException, InvoiceException, UserException {
        if (!(offering.getType().equals("product") || offering.getType().equals("service")))
            throw new OfferingException("Offering type should be product or service");
        Offering offeringFromDB = offeringRepository.findById(offering.getId()).orElseThrow(() -> new OfferingException("Offering with id " + offering.getId() + " doesn't exist"));
        if (!(offeringFromDB.getCreatedBy().getEmail().equals(currentUser.getEmail()) || currentUser.getRole().equals(UserRole.ADMIN.getKey())))
            throw new UserException("You try to update an offering that is not yours");
        offeringFromDB.setName(offering.getName());
        offeringFromDB.setDescription(offering.getDescription());
        offeringFromDB.setPrice(offering.getPrice());
        offeringFromDB.setType(offering.getType().equals("product") ? OfferingType.PRODUCT : OfferingType.SERVICE);
        return offeringFromDB.toDTO();
    }

    @Override
    public void remove(long id, UserDTO currentUser) throws UserException, OfferingException {
        Offering offeringFromDB = offeringRepository.findById(id).orElseThrow(() -> new OfferingException("Offering with id " + id + " doesn't exist"));
        if (!(offeringFromDB.getCreatedBy().getEmail().equals(currentUser.getEmail()) || currentUser.getRole().equals(UserRole.ADMIN.getKey())))
            throw new UserException("You try to remove an offering that is not yours");
        offeringRepository.deleteById(id);
    }
}
