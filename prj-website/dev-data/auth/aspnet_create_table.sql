CREATE TABLE AspNetRoles
(
    Id   nvarchar(128) NOT NULL,
    Name nvarchar(256) NOT NULL,
    PRIMARY KEY (Id)
);

CREATE TABLE AspNetUserClaims
(
    Id         int IDENTITY(1,1) NOT NULL,
    UserId     nvarchar(128) NOT NULL,
    ClaimType  nvarchar( max) NULL,
    ClaimValue nvarchar( max) NULL,
    PRIMARY KEY (Id)
);

CREATE TABLE AspNetUserLogins
(
    LoginProvider nvarchar(128) NOT NULL,
    ProviderKey   nvarchar(128) NOT NULL,
    UserId        nvarchar(128) NOT NULL,
    PRIMARY KEY (LoginProvider, ProviderKey, UserId)
);

CREATE TABLE AspNetUserRoles
(
    UserId nvarchar(128) NOT NULL,
    RoleId nvarchar(128) NOT NULL,
    PRIMARY KEY (UserId, RoleId)
);

CREATE TABLE AspNetUsers
(
    Id                   nvarchar(128) NOT NULL,
    Email                nvarchar(256) NULL,
    EmailConfirmed       bit NOT NULL,
    PasswordHash         nvarchar( max) NULL,
    SecurityStamp        nvarchar( max) NULL,
    PhoneNumber          nvarchar( max) NULL,
    PhoneNumberConfirmed bit NOT NULL,
    TwoFactorEnabled     bit NOT NULL,
    LockoutEndDateUtc    datetime NULL,
    LockoutEnabled       bit NOT NULL,
    AccessFailedCount    int NOT NULL,
    UserName             nvarchar(256) NOT NULL,
    PRIMARY KEY (Id)
);