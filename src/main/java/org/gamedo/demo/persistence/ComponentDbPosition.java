package org.gamedo.demo.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.gamedo.annotation.Update;
import org.gamedo.persistence.db.ComponentDbData;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@Document("player")
@AllArgsConstructor
@Accessors(chain = true)
@Update
public class ComponentDbPosition extends ComponentDbData
{
    private int x;
    private int y;
}