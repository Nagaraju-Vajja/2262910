package com.macys.uop.foundation.messagestore;

import com.google.cloud.Timestamp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;
/**
 * Entity that represents MessageStore table.
 */
@Getter
@Setter
@NoArgsConstructor
@Table(name = "MessageStore")
public class MessageStore implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@PrimaryKey
    @Column(name = "messageStorePk", nullable = false)
    private String messageStorePk;

    @Column(name = "clientId")
    private String clientId;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "messageId", nullable = false)
    private String messageId;

    @Column(name = "orderId", nullable = false)
    private String orderId;

    @Column(name = "createdBy")
    private String createdBy;

    @Column(name = "createdTs")
    private Timestamp createdTs;

    @Column(name = "lastUpdatedBy")
    private String lastUpdatedBy;

    @Column(name = "lastUpdatedTs", spannerCommitTimestamp = true)
    private Timestamp lastUpdatedTs;
}
