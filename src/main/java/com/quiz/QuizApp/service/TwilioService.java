package com.quiz.QuizApp.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TwilioService {

    @Value("${twilio.account.sid}")
    private String ACCOUNT_SID;

    @Value("${twilio.auth.token}")
    private String AUTH_TOKEN;

    @Value("${twilio.phone.number}")
    private String TWILIO_PHONE_NUMBER;

    @Value("${app.frontend.url}")
    private String FRONTEND_URL;

    public void sendQuizInvites(List<String> phoneNumbers, Long quizId) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        // Prepare the message content
        String messageBody = String.format(
                "You've been invited to take a quiz! Click the link below to participate:\n\n%s/quiz/respond?quizId=%d",
                FRONTEND_URL,
                quizId
        );

        // Loop through the list of phone numbers and send the SMS invite to each
        for (String phoneNumber : phoneNumbers) {
            PhoneNumber to = new PhoneNumber(formatPhoneNumber(phoneNumber));
            PhoneNumber from = new PhoneNumber(TWILIO_PHONE_NUMBER);

            // Send the SMS
            Message.creator(
                    to,
                    from,
                    messageBody
            ).create();
        }
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            return "+31" + phoneNumber.substring(1);
        }
        return phoneNumber;
    }
}
