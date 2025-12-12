--
-- (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
--
-- This file is part of the Kitodo project.
--
-- It is licensed under GNU General Public License version 3 or later.
--
-- For the full copyright and license information, please read the
-- GPL3-License.txt file that was distributed with this source code.
--

-- Add tables for keyword indices
CREATE TABLE tag (
    id INT PRIMARY KEY,
    tag VARCHAR(255) NOT NULL
);

CREATE TABLE process_x_tag (
    tag_id INT NOT NULL,
    process_id INT NOT NULL,
    PRIMARY KEY (tag_id, process_id),
    FOREIGN KEY (tag_id) REFERENCES tag(id),
    FOREIGN KEY (process_id) REFERENCES process(id);
);

CREATE TABLE titletag (
    id INT PRIMARY KEY,
    tag VARCHAR(255) NOT NULL
);

CREATE TABLE process_x_titletag (
    tag_id INT NOT NULL,
    process_id INT NOT NULL,
    PRIMARY KEY (tag_id, process_id),
    FOREIGN KEY (tag_id) REFERENCES titletag(id),
    FOREIGN KEY (process_id) REFERENCES process(id);
);

CREATE TABLE batchtag (
    id INT PRIMARY KEY,
    tag VARCHAR(255) NOT NULL
);

CREATE TABLE process_x_batchtag (
    tag_id INT NOT NULL,
    process_id INT NOT NULL,
    PRIMARY KEY (tag_id, process_id),
    FOREIGN KEY (tag_id) REFERENCES batchtag(id),
    FOREIGN KEY (process_id) REFERENCES process(id);
);
