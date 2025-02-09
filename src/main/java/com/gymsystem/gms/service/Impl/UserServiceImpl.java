package com.gymsystem.gms.service.Impl;

import com.gymsystem.gms.enumeration.Role;
import com.gymsystem.gms.exceptions.model.*;
import com.gymsystem.gms.model.Score;
import com.gymsystem.gms.model.User;
import com.gymsystem.gms.model.UserPrincipal;
import com.gymsystem.gms.repository.ScoreRepository;
import com.gymsystem.gms.repository.UserMembershipRepository;
import com.gymsystem.gms.repository.UserRepository;
import com.gymsystem.gms.repository.UserWorkoutRepository;
import com.gymsystem.gms.service.UserService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static com.gymsystem.gms.constraints.EmailConstant.*;
import static com.gymsystem.gms.constraints.FileConstant.*;
import static com.gymsystem.gms.constraints.UserImplConstant.*;
import static com.gymsystem.gms.enumeration.Role.ROLE_USER;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.MediaType.*;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;
    private final UserMembershipRepository userMembershipRepository;
    private final UserWorkoutRepository userWorkoutRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LoginAttemptServiceImpl loginAttemptService;
    private final ScoreRepository scoreRepository;

    @Value("${api.stripe.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init(){
        Stripe.apiKey = stripeApiKey;
    }

    @Autowired
    public UserServiceImpl(UserRepository userRepository,ScoreRepository scoreRepository, UserMembershipRepository userMembershipRepository,UserWorkoutRepository userWorkoutRepository, BCryptPasswordEncoder passwordEncoder, LoginAttemptServiceImpl loginAttemptService) {
        this.userRepository = userRepository;
        this.userWorkoutRepository = userWorkoutRepository;
        this.userMembershipRepository = userMembershipRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.scoreRepository = scoreRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsernameOrEmail(username,username);
        if(user == null){
            LOGGER.error(NO_USER_FOUND_BY_USERNAME + "{}", username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME+username);
        } else {
            validateLoginAttempt(user);
            user.setLastLoginDate(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info("User found by username: {}", username);
            return userPrincipal;
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email) throws UsernameNotFoundException, EmailExistException, UsernameExistException, UserNotFoundException, StripeException {
        validateNewUsernameAndEmail(StringUtils.EMPTY,username,email);
        User user = new User();

        Score score = new Score();
        score.setValue(new BigDecimal("0"));// Set the score value or other fields
        scoreRepository.save(score);

        user.setUserId(generateCustomerId(firstName + lastName, email));
        String password = generatePassword();
        String encodedPassword = encodedPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setScore(score);
        user.setRole(ROLE_USER.toString());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userRepository.save(user);
        LOGGER.info("New user password: "+password);
        sendEmailWithPassword(password,email);
        LOGGER.info("Email sent to: "+email);
        return user;
    }
    @Override
    public User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNotLocked, boolean isActive, MultipartFile profileFile) throws EmailExistException, UsernameExistException, IOException, NotAnImageFileException, UserNotFoundException, StripeException {
        validateNewUsernameAndEmail(EMPTY, username, email);
        User user = new User();
        Score score = new Score();
        score.setValue(new BigDecimal("0")); // Set the score value or other fields
        scoreRepository.save(score);
        String password = generatePassword();
        String encodedPassword = encodedPassword(password);
        user.setUserId(generateCustomerId(firstName + lastName, email));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setJoinDate(new Date());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setActive(isActive);
        user.setNotLocked(isNotLocked);
        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        user.setScore(score);
        userRepository.save(user);
        saveProfileImage(user, profileFile);
        LOGGER.info("New user password: " + password);
        sendEmailWithPassword(password, email);
        LOGGER.info("Email sent to: "+email);
        return user;
    }

    @Override
    public User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNotLocked, boolean isActive, MultipartFile profileFile) throws EmailExistException, UsernameExistException, IOException, NotAnImageFileException, UserNotFoundException {
        User currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNotLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepository.save(currentUser);
        saveProfileImage(currentUser, profileFile);
        return currentUser;
    }

    @Override
    public void deleteUser(Long id) throws IOException, StripeException {
        User user = userRepository.findUserById(id);
        Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
        FileUtils.deleteDirectory(new File(userFolder.toString()));
        userMembershipRepository.deleteAllByUserId(user);
        userWorkoutRepository.deleteAllByUser(user);
        userRepository.deleteById(id);
        Customer.retrieve(user.getUserId()).delete();
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException {
        User user = userRepository.findUserByEmail(email);
        if(user== null){
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        }
        String password = generatePassword();
        user.setPassword(encodedPassword(password));
        userRepository.save(user);
        LOGGER.info("New user password: " + password);
        sendEmailWithPassword(password,email);
        LOGGER.info("Email sent to: "+email);
    }

    @Override
    public void setNewPassword(String email,String oldPassword,String newPassword) throws EmailNotFoundException, WrongOldPasswordException {
        User user = userRepository.findUserByEmail(email);
        if(user== null){
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL);
        }
        if (!checkOldPassword(user, oldPassword)) {
            throw new WrongOldPasswordException("Incorrect old password");
        }
        user.setPassword(encodedPassword(newPassword));
        userRepository.save(user);
        LOGGER.info("New user password: " + newPassword);
    }

    @Override
    public User updateProfileImage(String username, MultipartFile profileImage) throws EmailExistException, UsernameExistException, IOException, NotAnImageFileException, UserNotFoundException {
        User user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImage(user, profileImage);
        return user;
    }

    @Override
    public User findUserByCustomerId(String userId) throws UserNotFoundException {
        return userRepository.findUserByUserId(userId).orElseThrow(() -> new UserNotFoundException(NO_USER_FOUND + userId));
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public User findUserByUsernameOrEmail(String username, String email) {
        return userRepository.findUserByUsernameOrEmail(username,email);
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findUserById(userId);
    }


    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UsernameNotFoundException, UsernameExistException, EmailExistException, UserNotFoundException {
        if (StringUtils.isNotEmpty(currentUsername)) {
            User currentUser = findUserByUsername(currentUsername);
            if (currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
            }
            User userByNewUsername = findUserByUsername(newUsername);
            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            User userByNewEmail = findUserByEmail(newEmail);
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            User userByNewUsername = userRepository.findUserByUsername(newUsername);
            if(userByNewUsername!=null){
                throw new UsernameExistException("Username already exists");
            }
            User userByNewEmail = userRepository.findUserByEmail(newEmail);
            if(userByNewEmail !=null){
                throw new EmailExistException("Email already exists");
            }
            return null;
        }
    }

    private void saveProfileImage(User user, MultipartFile profileFile) throws IOException, NotAnImageFileException {
        if (profileFile != null) {
            if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(profileFile.getContentType())) {
                throw new NotAnImageFileException(profileFile.getOriginalFilename() + NOT_AN_IMAGE_FILE);
            }
            Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
            if(!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                LOGGER.info(DIRECTORY_CREATED + "{}", userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXTENSION));
            Files.copy(profileFile.getInputStream(), userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));
            userRepository.save(user);
            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + "{}", profileFile.getOriginalFilename());
        }
    }

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH
                + username + DOT + JPG_EXTENSION).toUriString();
    }
    private void sendEmailWithPassword(String temporaryPassword, String email){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(SMTP_HOST);
        mailSender.setPort(DEFAULT_PORT);
        mailSender.setUsername(USERNAME);
        mailSender.setPassword(PASSWORD);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(FROM_EMAIL);
            helper.setTo(email);
            helper.setSubject("Your Temporary Gym Access Password");

            String htmlContent = """
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <div style="background-color: #f7f7f7; padding: 20px;">
                    <h1 style="color: #333; text-align: center;">Welcome to Our Gym! 🏋️‍♂️</h1>
                    <div style="background-color: white; padding: 20px; border-radius: 5px; margin-top: 20px;">
                        <p>Hello,</p>
                        
                        <p>Your temporary password for gym access has been created.</p>
                        
                        <div style="background-color: #f8f8f8; 
                                  padding: 15px; 
                                  border-left: 4px solid #4CAF50; 
                                  margin: 20px 0;">
                            <p style="margin: 0;"><strong>Temporary Password:</strong> %s</p>
                        </div>
                        
                        <p><strong>⚠️ IMPORTANT: For your security:</strong></p>
                        <ul>
                            <li>Change this password immediately after your first login</li>
                            <li>This temporary password will expire in 24 hours</li>
                            <li>Delete this email after changing your password</li>
                            <li>Never share your password with anyone</li>
                        </ul>
                        
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" 
                               style="background-color: #4CAF50; 
                                      color: white; 
                                      padding: 12px 25px; 
                                      text-decoration: none; 
                                      border-radius: 3px; 
                                      display: inline-block;">
                                Login to Your Account
                            </a>
                        </div>
                        
                        <p>If you didn't request this password, please contact us immediately.</p>
                        
                        <hr style="border: none; border-top: 1px solid #eee; margin: 20px 0;">
                        
                        <p style="color: #666; font-size: 14px;">
                            Need help? Contact our support team at support@gym.com
                        </p>
                    </div>
                    
                    <div style="text-align: center; margin-top: 20px; color: #666; font-size: 12px;">
                        <p>Our Gym Name</p>
                        <p>123 Fitness Street, Gym City, GC 12345</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(temporaryPassword, LOGIN_URL);

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password email", e);
        }
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    private String encodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private boolean checkOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(25);
    }

    private String generateCustomerId(String fullName, String email) throws StripeException {
        CustomerCreateParams params =
                CustomerCreateParams.builder()
                        .setName(fullName)
                        .setEmail(email)
                        .build();
        Customer customer = Customer.create(params);
        return customer.getId();
    }

    private void validateLoginAttempt(User user) {
        String username = user.getUsername();
        if(user.isNotLocked()){
            user.setNotLocked(!loginAttemptService.hasExceededMaxAttempts(username));
        }else {
            loginAttemptService.evicUserFromLoginAttemptCache(username);
        }
    }
}
