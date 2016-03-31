package com.clean.example.dataproviders.database.broadbandaccessdevice;

import org.junit.Test;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class BroadbandAccessDeviceDatabaseDataProviderTest {

    JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

    BroadbandAccessDeviceDatabaseDataProvider broadbandAccessDeviceDatabaseDataProvider = new BroadbandAccessDeviceDatabaseDataProvider(jdbcTemplate);

    @Test
    public void returnsEmptyListWhenThereAreNoDevices() throws Exception {
        givenThereAreNoDevices();

        List<String> allDeviceHostnames = broadbandAccessDeviceDatabaseDataProvider.getAllDeviceHostnames();

        assertThat(allDeviceHostnames).isEmpty();
    }

    @Test
    public void returnsTheHostnameOfAllDevices() throws Exception {
        thereThereAreDevices("hostname1", "hostname2", "hostname3");

        List<String> allDeviceHostnames = broadbandAccessDeviceDatabaseDataProvider.getAllDeviceHostnames();

        assertThat(allDeviceHostnames).containsOnly("hostname1", "hostname2", "hostname3");
    }

    @Test
    public void returnsNullSerialNumberForADeviceThatDoesNotExist() throws Exception {
        givenDeviceDoesNotExist("hostname1");

        String serialNumber = broadbandAccessDeviceDatabaseDataProvider.getSerialNumber("hostname1");

        assertThat(serialNumber).isNull();
    }

    @Test
    public void returnsSerialNumberOfADevice() throws Exception {
        givenDeviceHasSerialNumber("hostname1", "serialNumber1");

        String serialNumber = broadbandAccessDeviceDatabaseDataProvider.getSerialNumber("hostname1");

        assertThat(serialNumber).isEqualTo("serialNumber1");
    }

    @Test
    public void updatesTheSerialNumberOfADevice() throws Exception {
        broadbandAccessDeviceDatabaseDataProvider.updateSerialNumber("hostname1", "newSerialNumber");

        verify(jdbcTemplate).update(anyString(), eq("newSerialNumber"), eq("hostname1"));
    }

    private void givenThereAreNoDevices() {
        when(jdbcTemplate.queryForList(anyString(), eq(String.class))).thenReturn(new ArrayList<>());
    }

    private void givenDeviceDoesNotExist(String hostname) {
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(hostname))).thenThrow(new IncorrectResultSizeDataAccessException(1));
    }

    private void givenDeviceHasSerialNumber(String hostname, String serialNumber) {
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(hostname))).thenReturn(serialNumber);
    }

    private void thereThereAreDevices(String... hostnames) {
        when(jdbcTemplate.queryForList(anyString(), eq(String.class))).thenReturn(Arrays.asList(hostnames));
    }

}