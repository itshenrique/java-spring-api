package com.nito.api.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import jakarta.validation.Valid;

import com.nito.api.dtos.CreateUserDto;
import com.nito.api.dtos.UpdateUserDto;
import com.nito.api.models.UserModel;
import com.nito.api.services.UserService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("api/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<Object> saveUser(@RequestBody @Valid CreateUserDto userDto) {

        if (userService.validateUsername(userDto.getUsername()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already registered!");

        if (userService.validateEmail(userDto.getEmail()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered!");

        if (userService.validatePassword(userDto.getPassword()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered!");

        var userModel = new UserModel();
        BeanUtils.copyProperties(userDto, userModel);
        userModel.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userModel));
    }

    @GetMapping
    public ResponseEntity<Page<UserModel>> getAllUsers(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable(value = "id") Integer id) {
        Optional<UserModel> userOptional = userService.findById(id);

        /*
         * TODO - Melhorar tratamento de erros de api
         * https://www.toptal.com/java/spring-boot-rest-api-error-handling
         * https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
         */
        if (!userOptional.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");

        return ResponseEntity.status(HttpStatus.OK).body(userOptional.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "id") Integer id) {
        Optional<UserModel> userOptional = userService.findById(id);

        if (!userOptional.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");

        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "id") Integer id,
            @RequestBody @Valid UpdateUserDto userDto) {
        Optional<UserModel> userOptional = userService.findById(id);

        if (!userOptional.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");

        var userModel = new UserModel();
        BeanUtils.copyProperties(userDto, userModel);
        userModel.setId(userOptional.get().getId());
        userModel.setCreatedAt(userOptional.get().getCreatedAt());
        userModel.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setPassword(userOptional.get().getPassword());

        return ResponseEntity.status(HttpStatus.OK).body(userService.save(userModel));
    }

}
