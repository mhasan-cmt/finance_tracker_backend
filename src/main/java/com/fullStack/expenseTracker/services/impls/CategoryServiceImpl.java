package com.fullStack.expenseTracker.services.impls;

import com.fullStack.expenseTracker.services.CategoryService;
import com.fullStack.expenseTracker.services.TransactionTypeService;
import com.fullStack.expenseTracker.services.UserService;
import com.fullStack.expenseTracker.dto.requests.CategoryRequestDto;
import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.enums.ApiResponseStatus;
import com.fullStack.expenseTracker.exceptions.CategoryAlreadyExistsException;
import com.fullStack.expenseTracker.exceptions.CategoryNotFoundException;
import com.fullStack.expenseTracker.exceptions.CategoryServiceLogicException;
import com.fullStack.expenseTracker.exceptions.TransactionTypeNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserNotFoundException;
import com.fullStack.expenseTracker.models.Category;
import com.fullStack.expenseTracker.models.User;
import com.fullStack.expenseTracker.repository.CategoryRepository;
import com.fullStack.expenseTracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final TransactionTypeService transactionTypeService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<ApiResponseDto<?>> getCategories() {
        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        categoryRepository.findAll()
                )
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getCategoriesByUser(String email) throws UserNotFoundException {
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        categoryRepository.findByUserOrGlobal(user)
                )
        );
    }

    @Override
    public boolean existsCategory(int id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public Category getCategoryById(int id) throws CategoryNotFoundException {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id" + id));
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> addNewCategory(CategoryRequestDto categoryRequestDto)
            throws TransactionTypeNotFoundException, CategoryServiceLogicException, CategoryAlreadyExistsException, UserNotFoundException {

        if (categoryRepository.existsByCategoryNameAndTransactionType(
                categoryRequestDto.getCategoryName(),
                transactionTypeService.getTransactionById(categoryRequestDto.getTransactionTypeId()
                ))) {
            throw new CategoryAlreadyExistsException("Category already exists!");
        }

        // For admin-created categories, user is null (global categories)
        Category category = new Category(
                categoryRequestDto.getCategoryName(),
                transactionTypeService.getTransactionById(categoryRequestDto.getTransactionTypeId()),
                null,
                true
        );

        try {
            categoryRepository.save(category);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS, HttpStatus.CREATED, "Category has been successfully added!"
                    )
            );
        }catch(Exception e) {
            log.error("Failed to add new category: " + e.getMessage());
            throw new CategoryServiceLogicException("Failed to add new category: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> addUserCategory(CategoryRequestDto categoryRequestDto)
            throws TransactionTypeNotFoundException, CategoryServiceLogicException, CategoryAlreadyExistsException, UserNotFoundException {

        // Get user by ID
        User user;
        if (categoryRequestDto.getUserId() != null) {
            user = userRepository.findById(categoryRequestDto.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + categoryRequestDto.getUserId()));
        } else {
            throw new UserNotFoundException("User ID is required");
        }

        if (categoryRepository.existsByCategoryNameAndTransactionTypeAndUser(
                categoryRequestDto.getCategoryName(),
                transactionTypeService.getTransactionById(categoryRequestDto.getTransactionTypeId()),
                user)) {
            throw new CategoryAlreadyExistsException("Category already exists for this user!");
        }

        Category category = new Category(
                categoryRequestDto.getCategoryName(),
                transactionTypeService.getTransactionById(categoryRequestDto.getTransactionTypeId()),
                user,
                true
        );

        try {
            categoryRepository.save(category);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS, HttpStatus.CREATED, "Category has been successfully added!"
                    )
            );
        }catch(Exception e) {
            log.error("Failed to add new category: " + e.getMessage());
            throw new CategoryServiceLogicException("Failed to add new category: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> updateCategory(int categoryId, CategoryRequestDto categoryRequestDto)
            throws CategoryNotFoundException, TransactionTypeNotFoundException, CategoryServiceLogicException, UserNotFoundException {

        Category category = getCategoryById(categoryId);

        category.setCategoryName(categoryRequestDto.getCategoryName());
        category.setTransactionType(transactionTypeService.getTransactionById(categoryRequestDto.getTransactionTypeId()));

        // Update user if provided
        if (categoryRequestDto.getUserId() != null) {
            User user = userRepository.findById(categoryRequestDto.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + categoryRequestDto.getUserId()));
            category.setUser(user);
        }

        try {
            categoryRepository.save(category);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS, HttpStatus.CREATED, "Category has been successfully updated!"
                    )
            );
        }catch(Exception e) {
            log.error("Failed to update category: " + e.getMessage());
            throw new CategoryServiceLogicException("Failed to update category: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> enableOrDisableCategory(int categoryId)
            throws CategoryServiceLogicException, CategoryNotFoundException {
        Category category = getCategoryById(categoryId);

        try {

            category.setEnabled(!category.isEnabled());
            categoryRepository.save(category);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS, HttpStatus.OK, "Category has been updated successfully!"
                    )
            );
        }catch(Exception e) {
            log.error("Failed to enable/disable category: " + e.getMessage());
            throw new CategoryServiceLogicException("Failed to update category: Try again later!");
        }
    }

}
