/**
 * Copyright (C) 2018 Infinite Automation Software. All rights reserved.
 */
package com.infiniteautomation.mango.rest.v2.model.user;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.infiniteautomation.mango.rest.v2.model.AbstractVoModel;
import com.infiniteautomation.mango.rest.v2.model.time.TimePeriod;
import com.infiniteautomation.mango.rest.v2.model.time.TimePeriodType;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.rt.event.AlarmLevels;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.permission.Permission;

import io.swagger.annotations.ApiModelProperty;

/**
 *
 * @author Terry Packer
 */
public class UserModel extends AbstractVoModel<User> {

    private String username;
    private String password;
    private String email;
    private String phone;
    private boolean disabled;
    private String homeUrl;
    private Date lastLogin;
    private Date lastPasswordChange;
    private AlarmLevels receiveAlarmEmails;
    private boolean receiveOwnAuditEvents;
    private String timezone;
    private boolean muted;
    private Set<String> permissions;
    private String locale;
    private boolean passwordLocked;
    private String hashAlgorithm;
    private boolean sessionExpirationOverride;
    private TimePeriod sessionExpirationPeriod;
    
    @ApiModelProperty("List of system settings permission definitions this user has access to")
    private Set<String> grantedPermissions;

    public UserModel() {
        super();
    }
    public UserModel(User vo) {
        fromVO(vo);
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    //Not exposed
    public String getPassword() {
        return "";
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public boolean isDisabled() {
        return disabled;
    }
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    public String getHomeUrl() {
        return homeUrl;
    }
    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }
    public AlarmLevels getReceiveAlarmEmails() {
        return receiveAlarmEmails;
    }
    public void setReceiveAlarmEmails(AlarmLevels receiveAlarmEmails) {
        this.receiveAlarmEmails = receiveAlarmEmails;
    }
    public boolean isReceiveOwnAuditEvents() {
        return receiveOwnAuditEvents;
    }
    public void setReceiveOwnAuditEvents(boolean receiveOwnAuditEvents) {
        this.receiveOwnAuditEvents = receiveOwnAuditEvents;
    }
    public String getTimezone() {
        return timezone;
    }
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    public boolean isMuted() {
        return muted;
    }
    public void setMuted(boolean muted) {
        this.muted = muted;
    }
    public Set<String> getPermissions() {
        return permissions;
    }
    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
    public String getLocale() {
        return locale;
    }
    public void setLocale(String locale) {
        this.locale = locale;
    }
    public boolean isPasswordLocked() {
        return passwordLocked;
    }
    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public Date getLastLogin() {
        return this.lastLogin;
    }

    public Date getLastPasswordChange() {
        return this.lastPasswordChange;
    }

    public boolean isSessionExpirationOverride() {
        return sessionExpirationOverride;
    }
    
    public void setSessionExpirationOverride(boolean sessionExpirationOverride) {
        this.sessionExpirationOverride = sessionExpirationOverride;
    }

    public TimePeriod getSessionExpirationPeriod() {
        return sessionExpirationPeriod;
    }
    
    public void setSessionExpirationPeriod(TimePeriod sessionExpirationPeriod) {
        this.sessionExpirationPeriod = sessionExpirationPeriod;
    }

    public Set<String> getGrantedPermissions() {
        return grantedPermissions;
    }
    
    public void setGrantedPermissions(Set<String> grants) {
        this.grantedPermissions = grants;
    }
    
    public boolean isOldHashAlgorithm() {
        //New Users have null passwords
        if(password == null)
            return false;

        String algorithm = Common.extractHashAlgorithm(password);

        if (User.LOCKED_ALGORITHM.equals(algorithm)) {
            // not old algorithm, just locked
            return false;
        }

        String defaultAlgorithm = Common.getHashAlgorithm();
        return !defaultAlgorithm.equals(algorithm);
    }

    @Override
    protected User newVO() {
        return new User();
    }

    @Override
    public void fromVO(User vo) {
        super.fromVO(vo);
        this.username = vo.getUsername();
        this.password = vo.getPassword();
        this.email = vo.getEmail();
        this.phone = vo.getPhone();
        this.disabled = vo.isDisabled();
        this.homeUrl = vo.getHomeUrl();
        this.lastLogin = vo.getLastLogin() == 0 ? null : new Date(vo.getLastLogin());
        this.lastPasswordChange = new Date(vo.getPasswordChangeTimestamp());
        this.receiveAlarmEmails = vo.getReceiveAlarmEmails();
        this.timezone = vo.getTimezone();
        this.muted = vo.isMuted();
        this.receiveOwnAuditEvents = vo.isReceiveOwnAuditEvents();
        this.permissions = vo.getPermissionsSet();
        this.locale = vo.getLocale();
        this.passwordLocked = vo.isPasswordLocked();
        this.sessionExpirationOverride = vo.isSessionExpirationOverride();
        if(sessionExpirationOverride)
            this.sessionExpirationPeriod = new TimePeriod(vo.getSessionExpirationPeriods(), TimePeriodType.valueOf(vo.getSessionExpirationPeriodType()));
        Set<Permission> granted = vo.getGrantedPermissions();
        if(granted != null) {
            this.grantedPermissions = new HashSet<>(granted.size());
            for(Permission grant : granted)
                this.grantedPermissions.add(grant.getTypeName());
        }
    }

    @Override
    public User toVO() {
        User user = super.toVO();
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);
        user.setDisabled(disabled);
        user.setHomeUrl(homeUrl);
        user.setReceiveAlarmEmails(receiveAlarmEmails);
        user.setTimezone(timezone);
        user.setMuted(muted);
        user.setReceiveOwnAuditEvents(receiveOwnAuditEvents);
        if(permissions != null)
            user.setPermissionsSet(permissions);
        user.setLocale(locale);
        if(!StringUtils.isEmpty(hashAlgorithm)) {
            String password = this.password != null ? this.password : "";
            user.setPasswordHash(this.hashAlgorithm, password);
        }else if(!StringUtils.isEmpty(password)){
            user.setPlainTextPassword(password);
        }
        user.setSessionExpirationOverride(sessionExpirationOverride);
        if(sessionExpirationPeriod != null) {
            user.setSessionExpirationPeriods(sessionExpirationPeriod.getPeriods());
            if(sessionExpirationPeriod.getType() != null)
                user.setSessionExpirationPeriodType(sessionExpirationPeriod.getType().name());
        }
        return user;
    }
}
