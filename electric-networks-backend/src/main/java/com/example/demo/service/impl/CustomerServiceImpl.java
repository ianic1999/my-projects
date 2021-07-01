package com.example.demo.service.impl;

import com.example.demo.dto.CustomerDTO;
import com.example.demo.dto.request.CustomerRequest;
import com.example.demo.model.Customer;
import com.example.demo.model.Station;
import com.example.demo.model.exception.CustomerException;
import com.example.demo.model.exception.StationException;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.StationRepository;
import com.example.demo.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final StationRepository stationRepository;

    @Override
    public Page<CustomerDTO> getAll(int page, int perPage, String field, Sort.Direction order, String fullName, String contractNumber, String meter, long stationId, String address, String isCompleted) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        List<CustomerDTO> customersList = field == null
                ? customerRepository.findAll().stream().map(Customer::toDTO).collect(Collectors.toList())
                    : customerRepository.findAll(Sort.by(new Sort.Order(order, field, Sort.NullHandling.NULLS_LAST).nullsLast())).stream().map(Customer::toDTO).collect(Collectors.toList());
        if (fullName != null) {
            customersList = customersList.stream().filter(customer -> customer.getFullName().toLowerCase().contains(fullName.toLowerCase())).collect(Collectors.toList());
        }
        if (contractNumber != null) {
            customersList = customersList.stream().filter(customer -> customer.getContractNumber().toLowerCase().contains(contractNumber.toLowerCase())).collect(Collectors.toList());
        }
        if (meter != null) {
            customersList = customersList.stream().filter(customer -> customer.getMeter().toLowerCase().contains(meter.toLowerCase())).collect(Collectors.toList());
        }
        if (stationId > 0) {
            customersList = customersList.stream().filter(customer -> customer.getStation().getId() == stationId).collect(Collectors.toList());
        }
        if (address != null) {
            customersList = customersList.stream().filter(customer -> customer.getAddress().toLowerCase().contains(address.toLowerCase())).collect(Collectors.toList());
        }
        if (isCompleted != null) {
            boolean completed = isCompleted.equals("true");
            customersList = customersList.stream().filter(customer -> customer.isCompleted() == completed).collect(Collectors.toList());
        }
        int total = customersList.size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), total);

        List<CustomerDTO> output = new ArrayList<>();

        if (start <= end) {
            output = customersList.subList(start, end);
        }
        return new PageImpl<>(output, pageable, total);
    }

    @Override
    public CustomerDTO getById(long id) throws CustomerException {
        return customerRepository.findById(id).orElseThrow(() -> new CustomerException("Customer with id " + id + " doesn't exist")).toDTO();
    }

    @Override
    public CustomerDTO add(CustomerRequest customer) throws StationException {
        Station station = stationRepository.findById(customer.getStation()).orElseThrow(() -> new StationException("Station with id " + customer.getStation() + " doesn't exist"));
        Customer customerToAdd = Customer.builder()
                .contractNumber(customer.getContractNumber())
                .fullName(customer.getFullName())
                .address(customer.getAddress())
                .meter(customer.getMeter())
                .phone(customer.getPhone())
                .station(station)
                .indications(new ArrayList<>())
                .build();
        return customerRepository.save(customerToAdd).toDTO();
    }

    @Override
    @Transactional
    public CustomerDTO update(CustomerRequest customer) throws StationException, CustomerException {
        Station station = stationRepository.findById(customer.getStation()).orElseThrow(() -> new StationException("Station with id " + customer.getStation() + " doesn't exist"));
        Customer customerFromDB = customerRepository.findById(customer.getId()).orElseThrow(() -> new CustomerException("Customer with id " + customer.getId() + " doesn't exist"));
        customerFromDB.setContractNumber(customer.getContractNumber());
        customerFromDB.setFullName(customer.getFullName());
        customerFromDB.setAddress(customer.getAddress());
        customerFromDB.setMeter(customer.getMeter());
        customerFromDB.setPhone(customer.getPhone());
        customerFromDB.setStation(station);
        return customerFromDB.toDTO();
    }

    @Override
    public void remove(long id) {
        customerRepository.deleteById(id);
    }

    @Override
    public int addFromXLS(long stationId, MultipartFile file) throws IOException, StationException, CustomerException {
        if (file == null)
            throw new IOException("Fisierul este nul");
        if (!file.getOriginalFilename().endsWith(".xlsx"))
            throw new IOException("Fisierul trebuie sa fie in formatul .xlsx");
        Station station = stationRepository.findById(stationId).orElseThrow(() -> new StationException("Station with id " + stationId + " doesn't exist"));
        File f = new File(file.getOriginalFilename());
        int response = 0;
        FileOutputStream outputStream = new FileOutputStream(f);
        outputStream.write(file.getBytes());
        outputStream.close();
        FileInputStream inputStream = new FileInputStream(f);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet) {
            Customer customer = new Customer();
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                try {
                    if (cell.getColumnIndex() == 0) {
                        customer.setContractNumber(String.valueOf((int) cell.getNumericCellValue()));
                    } else if (cell.getColumnIndex() == 1) {
                        customer.setFullName(cell.getStringCellValue());
                    } else if (cell.getColumnIndex() == 2) {
                        customer.setAddress(cell.getStringCellValue());
                    } else if (cell.getColumnIndex() == 3) {
                        customer.setMeter(String.valueOf((int) cell.getNumericCellValue()));
                    } else if (cell.getColumnIndex() == 4) {
                        customer.setPhone(String.valueOf((int) cell.getNumericCellValue()));
                    }
                } catch (IllegalStateException e) {
                    String type = cell.getCellType().name();
                    String correctType;
                    if (cell.getColumnIndex() == 0 || cell.getColumnIndex() == 3 || cell.getColumnIndex() == 4)
                        correctType = "NUMAR";
                    else
                        correctType = "TEXT";
                    throw new CustomerException("Linia " + (cell.getRowIndex() + 1) + ", coloana " + (cell.getColumnIndex() + 1) + ": Tip cerut: " + correctType + " , tip receptionat: " + (type.equals("NUMERIC") ? "NUMAR" : "TEXT"));
                }
            }
            customer.setStation(station);
            customer.setIndications(new ArrayList<>());
            customerRepository.save(customer);
            response++;
        }
        inputStream.close();
        workbook.close();
        f.delete();
        return response;
    }
}
