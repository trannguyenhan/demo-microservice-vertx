package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Product {
  private Integer id;
  private String name;
  private String description;
  private float price;
//  private LocalDateTime createdAt;
//  private LocalDateTime updatedAt;
}
