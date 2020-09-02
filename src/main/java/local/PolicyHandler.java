package local;

import local.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyHandler{

    @Autowired
    ScreeningRepository screeningRepository;
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }


    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverHospitalDeleted_ForceCancel(@Payload HospitalDeleted hospitalDeleted){

        if(hospitalDeleted.isMe()){
            System.out.println("##### listener ForceCancel : " + hospitalDeleted.toJson());
            List<Screening> list = screeningRepository.findByHospitalId(hospitalDeleted.getId());
            for(Screening temp : list){
                // 본인이 취소한건은 제외
                if(!"CANCELED".equals(temp.getStatus())) {
                    temp.setStatus("FORCE_CANCELED");
                    screeningRepository.save(temp);
                }
            }
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReservationCompleted_RequestComplete(@Payload ReservationCompleted reservationCompleted){

        if(reservationCompleted.isMe()){
            System.out.println("##### listener RequestComplete : " + reservationCompleted.toJson());

            Optional<Screening> temp = screeningRepository.findById(reservationCompleted.getScreeningId());
            Screening target = temp.get();
            System.out.println("##### RequestComplete : " + target.toString());
            target.setHospitalId(Long.parseLong(reservationCompleted.getHospitalId()));
            target.setStatus(reservationCompleted.getStatus());
            screeningRepository.save(target);
        }
    }

}
