package com.example.demo.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.gamedo.persistence.db.ComponentDbData;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@Document("player")
@AllArgsConstructor
public class ComponentDbPosition extends ComponentDbData
{
    private int x;
    private int y;
}