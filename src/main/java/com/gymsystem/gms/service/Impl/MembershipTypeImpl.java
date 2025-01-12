package com.gymsystem.gms.service.Impl;


import com.gymsystem.gms.enumeration.UnitOfTime;
import com.gymsystem.gms.exceptions.model.MembershipTypeExistException;
import com.gymsystem.gms.exceptions.model.MembershipTypeNameNotUniqueException;
import com.gymsystem.gms.exceptions.model.MembershipTypeNotFoundException;
import com.gymsystem.gms.model.MembershipType;
import com.gymsystem.gms.repository.MembershipTypeRepository;
import com.gymsystem.gms.service.MembershipTypeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Product;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.ProductUpdateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;

import static com.gymsystem.gms.constraints.MembershipType.*;

@Service
@Transactional
public class MembershipTypeImpl implements MembershipTypeService {

    MembershipTypeRepository membershipTypeRepository;

    @Value("${api.stripe.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init(){
        Stripe.apiKey = stripeApiKey;
    }

    @Autowired
    public MembershipTypeImpl(MembershipTypeRepository membershipTypeRepository) {
        this.membershipTypeRepository = membershipTypeRepository;
    }

    @Override
    public List<MembershipType> getMembershipTypes() {
        return membershipTypeRepository.findAll();
    }

    @Override
    public MembershipType findMembershipTypeById(Long membershipTypeId) {
        return membershipTypeRepository.findMembershipTypeById(membershipTypeId);
    }

    @Override
    public MembershipType addMembershipType(String name, String description, String type, Long price, Integer validityPeriodNumber,String validityUnitOfTime) throws MembershipTypeExistException, MembershipTypeNameNotUniqueException, StripeException {
        checkIfNameIsUnique(name);
        MembershipType membershipType = checkIfMembershipTypeExists(name,price);
        membershipType.setName(name);
        membershipType.setPrice(price);
        membershipType.setValidityPeriodNumber(validityPeriodNumber);
        membershipType.setValidityUnitOfTime(getUnitOfTimeEnumName(validityUnitOfTime));
        membershipType.setDescription(description);
        membershipType.setType(type);
        membershipType.setMembershipProductId(createProduct(name, description, price).getId());
        membershipTypeRepository.save(membershipType);
        return membershipType;
    }

    @Override
    public MembershipType updateMembershipType(String oldName, String newName , String newDescription, String newType , Long newPrice, Integer validityPeriodNumber,String validityUnitOfTime) throws MembershipTypeNotFoundException, MembershipTypeNameNotUniqueException {
        if(!oldName.equals(newName)){
            checkIfNameIsUnique(newName);
        }
        MembershipType membershipType = findMembershipTypeByName(oldName);
        membershipType.setName(newName);
        membershipType.setPrice(newPrice);
        membershipType.setValidityPeriodNumber(validityPeriodNumber);
        membershipType.setValidityUnitOfTime(getUnitOfTimeEnumName(validityUnitOfTime));
        membershipType.setDescription(newDescription);
        membershipType.setType(newType);
        membershipTypeRepository.save(membershipType);
        return membershipType;
    }

    @Override
    public MembershipType findMembershipTypeByName(String name) throws MembershipTypeNotFoundException {
        MembershipType membershipType = membershipTypeRepository.findMembershipTypeByName(name);
        if(membershipType ==  null){
            throw new MembershipTypeNotFoundException(MEMBERSHIP_TYPE_NOT_FOUND);
        }
        return membershipType;
    }

    @Override
    public void deleteMembershipType(Long id) throws StripeException {
        MembershipType membershipType = membershipTypeRepository.findMembershipTypeById(id);
        membershipTypeRepository.deleteById(id);

        Product product = Product.retrieve(membershipType.getMembershipProductId());
        ProductUpdateParams params = ProductUpdateParams.builder().setActive(false).build();
        product.update(params);
    }


    private MembershipType checkIfMembershipTypeExists(String name, Long price) throws MembershipTypeExistException {
        MembershipType membershipType = membershipTypeRepository.findMembershipTypeByNameAndPrice(name,price);
        if(membershipType!=null){
            throw new MembershipTypeExistException(MEMBERSHIP_TYPE_ALREADY_EXISTS);
        }
        else{
            return new MembershipType();
        }
    }
    private UnitOfTime getUnitOfTimeEnumName(String role) {
        return UnitOfTime.valueOf(role.toUpperCase());
    }
    private void checkIfNameIsUnique(String name) throws MembershipTypeNameNotUniqueException {
        MembershipType membershipType = membershipTypeRepository.findMembershipTypeByName(name);
        if(membershipType !=  null){
            throw new MembershipTypeNameNotUniqueException(MEMBERSHIP_NAME_IS_NOT_UNIQUE);
        }
    }

    private Product createProduct(String name, String description, Long price) throws StripeException {
        ProductCreateParams params = ProductCreateParams.builder()
                .setName(name)
                .setActive(true)
                .setDescription(description)
                .setDefaultPriceData(ProductCreateParams.DefaultPriceData.builder()
                        .setUnitAmount(price * 100)
                        .setCurrency("pln")
                        .build())
                .build();
        return Product.create(params);
    }
}
