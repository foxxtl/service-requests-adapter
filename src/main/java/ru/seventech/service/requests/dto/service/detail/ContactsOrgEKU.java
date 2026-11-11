package ru.seventech.service.requests.dto.service.detail;

import ru.seventech.service.requests.dto.OrgDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContactsOrgEKU {

    private Integer orderContactsOrgEKU;
    private String division;
    private String fullName;
    private String positione;
    private String email;
    private String phone;
    private String address;
    private String site;
    private String contactsAcceptFrom;
    private String contactsAcceptTo;
    private String contactsAcceptComment;
    private OrgDto organization;


}
