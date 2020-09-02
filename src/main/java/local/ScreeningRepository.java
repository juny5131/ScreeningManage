package local;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ScreeningRepository extends PagingAndSortingRepository<Screening, Long>{

    List<Screening> findByHospitalId(Long HospitalId);
}