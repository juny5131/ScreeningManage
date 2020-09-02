package local;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;

@Entity
@Table(name="Screening_table")
public class Screening {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long hospitalId;
    private String chkDate;
    private String custNm;
    private String status;
    private String hospitalNm;

    @PostPersist
    public void onPostPersist(){;

        // 검진 요청함 ( Req / Res : 동기 방식 호출)
        local.external.Hospital hospital = new local.external.Hospital();
        hospital.setHospitalId(getHospitalId());
        // mappings goes here
        ScreeningManageApplication.applicationContext.getBean(local.external.HospitalService.class)
            .screeningRequest(hospital.getHospitalId(),hospital);


        Requested requested = new Requested();
        BeanUtils.copyProperties(this, requested);
        requested.publishAfterCommit();
    }


    @PostUpdate
    public void onPostUpdate(){

        System.out.println("#### onPostUpdate :" + this.toString());

        if("CANCELED".equals(this.getStatus())) {
            Canceled canceled = new Canceled();
            BeanUtils.copyProperties(this, canceled);
            canceled.publishAfterCommit();
        }
        else if("FORCE_CANCELED".equals(getStatus())){
            ForceCanceled forceCanceled = new ForceCanceled();
            BeanUtils.copyProperties(this, forceCanceled);
            forceCanceled.publishAfterCommit();
        }
        else if("REQUEST_COMPLETED".equals(getStatus())){
            System.out.println(getStatus());
            System.out.println("## REQ Info : " + this.getHospitalId());
            System.out.println("## REQ Info : " + this.getHospitalId());
            RequestCompleted requestCompleted = new RequestCompleted();
            BeanUtils.copyProperties(this, requestCompleted);
            requestCompleted.publishAfterCommit();
        }

    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
    }
    public String getChkDate() {
        return chkDate;
    }

    public void setChkDate(String chkDate) {
        this.chkDate = chkDate;
    }
    public String getCustNm() {
        return custNm;
    }

    public void setCustNm(String custNm) {
        this.custNm = custNm;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getHospitalNm() {
        return hospitalNm;
    }

    public void setHospitalNm(String hospitalNm) {
        this.hospitalNm = hospitalNm;
    }




}
