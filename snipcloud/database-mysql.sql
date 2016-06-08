CREATE TABLE IF NOT EXISTS `hastag` (
  `snippet` int(64) NOT NULL,
  `tag` int(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `ratings` (
  `users` int(11) NOT NULL,
  `snippet` int(64) NOT NULL,
  `rates` int(11) DEFAULT NULL,
  `note` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `snippets` (
`id` int(64) NOT NULL,
  `code` text,
  `vote` int(11) NOT NULL DEFAULT '0',
  `usecount` int(11) NOT NULL DEFAULT '0',
  `author` int(11) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `tags` (
`id` int(32) NOT NULL,
  `tag` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `users` (
`id` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password_hash` varchar(255) NOT NULL,
  `password_salt` varchar(255) NOT NULL,
  `verification_token` varchar(255) NOT NULL,
  `verified` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

INSERT INTO `users` (`id`, `username`, `firstname`, `lastname`, `email`, `password_hash`, `password_salt`, `verification_token`, `verified`) VALUES
(1, 'snipmine', 'Snip', 'Mine', 'alex@alsclo.de', 'de77a76f6d77ca37c19c94619a7482f7849943bd', '8cfec14e261db469cade1facbf0786cbfa0fab0c', '99247c59593ad8b7d2b0dd7df758050249411855', 1);


ALTER TABLE `hastag`
 ADD PRIMARY KEY (`snippet`,`tag`);

ALTER TABLE `ratings`
 ADD PRIMARY KEY (`users`,`snippet`);

ALTER TABLE `snippets`
 ADD PRIMARY KEY (`id`);

ALTER TABLE `tags`
 ADD PRIMARY KEY (`id`), ADD KEY `tag` (`tag`);

ALTER TABLE `users`
 ADD PRIMARY KEY (`id`);


ALTER TABLE `snippets`
MODIFY `id` int(64) NOT NULL AUTO_INCREMENT;
ALTER TABLE `tags`
MODIFY `id` int(32) NOT NULL AUTO_INCREMENT;
ALTER TABLE `users`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
