package com.hanghae99.boilerplate.noti.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.*;
import com.hanghae99.boilerplate.board.domain.FcmTarget;
import com.hanghae99.boilerplate.board.domain.FcmTargetRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class FCMService {

    private final Map<Long, String> tokenMap = new HashMap<>();
    private final Map<String, String> messageMap = new HashMap<>();
    private final FcmTargetRepository fcmTargetRepository;

    //@Transactional
    public void register(final Long userId, final String token) {
//        tokenMap.put(userId, token);
//        String targetToken = getToken(userId);
//        System.out.println("fddfdfdfdfd");
//        System.out.println(targetToken);
        Optional<FcmTarget> fcmTarget = fcmTargetRepository.findByMemberId(userId);

        if (fcmTarget.isEmpty()){
            FcmTarget fcmTarget1 = FcmTarget.builder()
                    .targetToken(token)
                    .memberId(userId)
                    .build();
            fcmTargetRepository.save(fcmTarget1);
            logger.info("저장됨?새로저장!!!!" + fcmTarget1.getTargetToken());

        }else{
            FcmTarget fcmTarget1 = fcmTarget.get();
            fcmTarget1.setTargetToken(token);
            logger.info("set저장"+fcmTarget1.getTargetToken()+"----"+userId);
            fcmTargetRepository.save(fcmTarget1);
            logger.info("set저장"+fcmTarget1.getTargetToken()+"----"+userId);

            
        }
        logger.info("저장됨?" + fcmTarget.get().getTargetToken());

    }
    
    public String getToken(Long id){
        return tokenMap.get(id);
    }

    private static final Logger logger = LoggerFactory.getLogger(FCMService.class);
    private static final String FIREBASE_CONFIG_PATH = "boiler-e3497-firebase-adminsdk-gedd3-21e30622fd.json";

    private final String API_URL= "https://fcm.google.api/v1/projects/boiler-e3497/message:send";
    private final ObjectMapper objectMapper;

    private static String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream(FIREBASE_CONFIG_PATH))
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        googleCredentials.refreshIfExpired();;
        return googleCredentials.getAccessToken().getTokenValue();
    }

    public void sendMessageTo(Long userId, String title, String body) throws ExecutionException, InterruptedException, JsonProcessingException {
        //FCM에 대한 액세스를 승인하려면
        // This registration token comes from the client FCM SDKs.
        //String registrationToken = "YOUR_REGISTRATION_TOKEN";

        // See documentation on defining a message payload.
        // link?
//        try{
//            System.out.println("fcmfcmfcmfcm");
//            logger.info("asdfasdfa"+"asdfasdfa"+"asdfasdf");
//
//            String targetToken = getToken(userId);
//            System.out.println(messageMap);
//            System.out.println(targetToken);
//            System.out.println(userId);
//            System.out.println(tokenMap);
//            System.out.println("sdfsdfsdfsdfdsfsdfsd");
//            messageMap.put("Df","Df");
//            String _message = makeMessage(targetToken, title, body);
//            Message message = Message.builder()
//                    .setToken(targetToken)
//                    .setWebpushConfig(WebpushConfig.builder().putHeader("ttl", "300").putAllData(
//                            messageMap
//                    ).build())
//                    .setNotification(new Notification(title, body))
//                    .build();
//
//            // Send a message to the device corresponding to the provided
//            // registration token.
//            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
//            logger.info("Sent message: " + response);
//            System.out.println();
//            //    {
//            //      "name":"projects/myproject-b5ae1/messages/0:1500415314455276%31bd1c9631bd1c96"
//            //    }
//
//        }catch (Exception e){
//
//            logger.info("fcm 아이디 존재하지 않음");
//        }
        try{
            logger.info("fcm 새롭게 새롭게확인하기");

            System.out.println("fcmfcmfcmfcm");
            logger.info("asdfasdfa"+"asdfasdfa"+"asdfasdf");

            String targetToken = getToken(userId);
            System.out.println(messageMap);
            System.out.println(targetToken);
            System.out.println(userId);
            System.out.println(tokenMap);
            System.out.println("sdfsdfsdfsdfdsfsdfsd");
            String _message = makeMessage(targetToken, title, body);

            //Optional<Member> member = memberRepository.findById(userId);
            //System.out.println("sdfsdfsdfsdfdsfsdfsd"+member);

            Optional<FcmTarget> fcmTarget = fcmTargetRepository.findByMemberId(userId);
            System.out.println("fcmTarge" + fcmTarget.get().getTargetToken());

            Message message = Message.builder()
                    .setToken(fcmTarget.get().getTargetToken())
                    .setWebpushConfig(WebpushConfig.builder().putHeader("ttl", "300").putAllData(
                            messageMap
                    ).build())
                    .setNotification(new Notification(title, body))
                    .build();

            System.out.println("me99999999s"+message);

            // Send a message to the device corresponding to the provided
            // registration token.
            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            logger.info("Sent message: " + response);
            System.out.println();
            //    {
            //      "name":"projects/myproject-b5ae1/messages/0:1500415314455276%31bd1c9631bd1c96"
            //    }

        }catch (Exception e){
            System.out.println("qwerqwerwer");
            System.out.println(e);
            logger.info("fcm 아이디 존재하지 않음");
        }

    }

    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder().
                        token(targetToken).notification(
                                FcmMessage.Notification.builder()
                                        .title(title)
                                        .body(body)
                                        .image(null)
                                        .build()

                        ).build()
                ).validate_only(false)
                .build();
        return objectMapper.writeValueAsString(fcmMessage);
    }

}
