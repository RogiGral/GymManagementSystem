package com.gymsystem.gms.service.Impl;

import com.gymsystem.gms.exceptions.model.UserNotFoundException;
import com.gymsystem.gms.model.User;
import com.gymsystem.gms.repository.ScoreRepository;
import com.gymsystem.gms.repository.UserRepository;
import com.gymsystem.gms.service.ScoreService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;

import static com.gymsystem.gms.constraints.UserImplConstant.NO_USER_FOUND;

@Service
@Transactional
public class ScoreServiceImpl implements ScoreService {
    //private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final ScoreRepository scoreRepository;
    private final UserRepository userRepository;

    private int calculatePoints(String difficulty) {
        return switch (difficulty.toLowerCase()) {
            case "beginner" -> 10;
            case "easy" -> 20;
            case "medium" -> 30;
            case "hard" -> 40;
            case "very_hard" -> 50;
            default -> 0;
        };
    }

    public ScoreServiceImpl(ScoreRepository scoreRepository, UserRepository userRepository) {
        this.scoreRepository = scoreRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BigDecimal getUserScore(String username) {
        return userRepository.findUserByUsername(username).getScore().getValue();
    }

    public void addPointsToScore(String username, String difficulty) throws UserNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if(user == null || user.getScore() == null){
            throw new UserNotFoundException(NO_USER_FOUND + "by username " + username);
        }
        Long scoreId = user.getScore().getId();
        scoreRepository.findById(scoreId).ifPresent(scoreEntity-> {
            BigDecimal newScoreValue = scoreEntity.getValue().add(BigDecimal.valueOf(calculatePoints(difficulty)));
            scoreEntity.setValue(newScoreValue);
            scoreRepository.save(scoreEntity);
        });
    }

    @Override
    public void removePointsFromScore(String username, String difficulty) throws UserNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if(user == null || user.getScore() == null){
            throw new UserNotFoundException(NO_USER_FOUND + "by username " + username);
        }
        Long scoreId = user.getScore().getId();
            scoreRepository.findById(scoreId).ifPresent(scoreEntity-> {
                BigDecimal newScoreValue = scoreEntity.getValue().subtract(BigDecimal.valueOf(calculatePoints(difficulty)));
                scoreEntity.setValue(newScoreValue);
                scoreRepository.save(scoreEntity);
            });
        }
}
