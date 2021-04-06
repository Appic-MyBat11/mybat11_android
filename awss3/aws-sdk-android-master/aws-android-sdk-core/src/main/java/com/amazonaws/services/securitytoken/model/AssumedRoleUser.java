/*
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazonaws.services.securitytoken.model;

import java.io.Serializable;

/**
 * <p>
 * The identifiers for the temporary security credentials that the operation
 * returns.
 * </p>
 */
public class AssumedRoleUser implements Serializable {
    /**
     * <p>
     * A unique identifier that contains the role ID and the role session name
     * of the role that is being assumed. The role ID is generated by AWS when
     * the role is created.
     * </p>
     * <p>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>2 - 193<br/>
     * <b>Pattern: </b>[\w+=,.@:-]*<br/>
     */
    private String assumedRoleId;

    /**
     * <p>
     * The ARN of the temporary security credentials that are returned from the
     * <a>AssumeRole</a> action. For more information about ARNs and how to use
     * them in policies, see <a href=
     * "https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html"
     * >IAM Identifiers</a> in the <i>IAM User Guide</i>.
     * </p>
     * <p>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>20 - 2048<br/>
     * <b>Pattern: </b>[
     * -\u007E\u0085\u00A0-\uD7FF\uE000-\uFFFD\u10000-\u10FFFF]+<br/>
     */
    private String arn;

    /**
     * <p>
     * A unique identifier that contains the role ID and the role session name
     * of the role that is being assumed. The role ID is generated by AWS when
     * the role is created.
     * </p>
     * <p>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>2 - 193<br/>
     * <b>Pattern: </b>[\w+=,.@:-]*<br/>
     *
     * @return <p>
     *         A unique identifier that contains the role ID and the role
     *         session name of the role that is being assumed. The role ID is
     *         generated by AWS when the role is created.
     *         </p>
     */
    public String getAssumedRoleId() {
        return assumedRoleId;
    }

    /**
     * <p>
     * A unique identifier that contains the role ID and the role session name
     * of the role that is being assumed. The role ID is generated by AWS when
     * the role is created.
     * </p>
     * <p>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>2 - 193<br/>
     * <b>Pattern: </b>[\w+=,.@:-]*<br/>
     *
     * @param assumedRoleId <p>
     *            A unique identifier that contains the role ID and the role
     *            session name of the role that is being assumed. The role ID is
     *            generated by AWS when the role is created.
     *            </p>
     */
    public void setAssumedRoleId(String assumedRoleId) {
        this.assumedRoleId = assumedRoleId;
    }

    /**
     * <p>
     * A unique identifier that contains the role ID and the role session name
     * of the role that is being assumed. The role ID is generated by AWS when
     * the role is created.
     * </p>
     * <p>
     * Returns a reference to this object so that method calls can be chained
     * together.
     * <p>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>2 - 193<br/>
     * <b>Pattern: </b>[\w+=,.@:-]*<br/>
     *
     * @param assumedRoleId <p>
     *            A unique identifier that contains the role ID and the role
     *            session name of the role that is being assumed. The role ID is
     *            generated by AWS when the role is created.
     *            </p>
     * @return A reference to this updated object so that method calls can be
     *         chained together.
     */
    public AssumedRoleUser withAssumedRoleId(String assumedRoleId) {
        this.assumedRoleId = assumedRoleId;
        return this;
    }

    /**
     * <p>
     * The ARN of the temporary security credentials that are returned from the
     * <a>AssumeRole</a> action. For more information about ARNs and how to use
     * them in policies, see <a href=
     * "https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html"
     * >IAM Identifiers</a> in the <i>IAM User Guide</i>.
     * </p>
     * <p>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>20 - 2048<br/>
     * <b>Pattern: </b>[
     * -\u007E\u0085\u00A0-\uD7FF\uE000-\uFFFD\u10000-\u10FFFF]+<br/>
     *
     * @return <p>
     *         The ARN of the temporary security credentials that are returned
     *         from the <a>AssumeRole</a> action. For more information about
     *         ARNs and how to use them in policies, see <a href=
     *         "https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html"
     *         >IAM Identifiers</a> in the <i>IAM User Guide</i>.
     *         </p>
     */
    public String getArn() {
        return arn;
    }

    /**
     * <p>
     * The ARN of the temporary security credentials that are returned from the
     * <a>AssumeRole</a> action. For more information about ARNs and how to use
     * them in policies, see <a href=
     * "https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html"
     * >IAM Identifiers</a> in the <i>IAM User Guide</i>.
     * </p>
     * <p>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>20 - 2048<br/>
     * <b>Pattern: </b>[
     * -\u007E\u0085\u00A0-\uD7FF\uE000-\uFFFD\u10000-\u10FFFF]+<br/>
     *
     * @param arn <p>
     *            The ARN of the temporary security credentials that are
     *            returned from the <a>AssumeRole</a> action. For more
     *            information about ARNs and how to use them in policies, see <a
     *            href=
     *            "https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html"
     *            >IAM Identifiers</a> in the <i>IAM User Guide</i>.
     *            </p>
     */
    public void setArn(String arn) {
        this.arn = arn;
    }

    /**
     * <p>
     * The ARN of the temporary security credentials that are returned from the
     * <a>AssumeRole</a> action. For more information about ARNs and how to use
     * them in policies, see <a href=
     * "https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html"
     * >IAM Identifiers</a> in the <i>IAM User Guide</i>.
     * </p>
     * <p>
     * Returns a reference to this object so that method calls can be chained
     * together.
     * <p>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>20 - 2048<br/>
     * <b>Pattern: </b>[
     * -\u007E\u0085\u00A0-\uD7FF\uE000-\uFFFD\u10000-\u10FFFF]+<br/>
     *
     * @param arn <p>
     *            The ARN of the temporary security credentials that are
     *            returned from the <a>AssumeRole</a> action. For more
     *            information about ARNs and how to use them in policies, see <a
     *            href=
     *            "https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html"
     *            >IAM Identifiers</a> in the <i>IAM User Guide</i>.
     *            </p>
     * @return A reference to this updated object so that method calls can be
     *         chained together.
     */
    public AssumedRoleUser withArn(String arn) {
        this.arn = arn;
        return this;
    }

    /**
     * Returns a string representation of this object; useful for testing and
     * debugging.
     *
     * @return A string representation of this object.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (getAssumedRoleId() != null)
            sb.append("AssumedRoleId: " + getAssumedRoleId() + ",");
        if (getArn() != null)
            sb.append("Arn: " + getArn());
        sb.append("}");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hashCode = 1;

        hashCode = prime * hashCode
                + ((getAssumedRoleId() == null) ? 0 : getAssumedRoleId().hashCode());
        hashCode = prime * hashCode + ((getArn() == null) ? 0 : getArn().hashCode());
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;

        if (obj instanceof AssumedRoleUser == false)
            return false;
        AssumedRoleUser other = (AssumedRoleUser) obj;

        if (other.getAssumedRoleId() == null ^ this.getAssumedRoleId() == null)
            return false;
        if (other.getAssumedRoleId() != null
                && other.getAssumedRoleId().equals(this.getAssumedRoleId()) == false)
            return false;
        if (other.getArn() == null ^ this.getArn() == null)
            return false;
        if (other.getArn() != null && other.getArn().equals(this.getArn()) == false)
            return false;
        return true;
    }
}